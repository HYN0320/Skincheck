package com.skincheck_backend.analysis.service;

import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.service.UserService;
import com.skincheck_backend.analysis.dto.*;
import com.skincheck_backend.analysis.entity.SkinAnalysis;
import com.skincheck_backend.analysis.entity.SkinAnalysisCondition;
import com.skincheck_backend.analysis.provider.AiResultProvider;
import com.skincheck_backend.analysis.repository.SkinAnalysisConditionRepository;
import com.skincheck_backend.analysis.repository.SkinAnalysisRepository;
import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SkinAnalysisService {

    private final AiResultProvider aiResultProvider;
    private final UserService userService;
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final SkinAnalysisConditionRepository conditionRepository;

    private final ConditionLevelCalculator levelCalculator;
    private final SummaryGenerator summaryGenerator;
    private final RegionMetricMapper regionMetricMapper;
    private final DeepSeekInsightClient deepSeekInsightClient;
    private final InsightPromptBuilder insightPromptBuilder;

    /**
     * ✅ 이미지 업로드 후 분석 + 저장
     */
    @Transactional
    public SkinAnalysisResultResponse analyzeAndSave(String email, String imageUrl) {

        User user = userService.getByEmailOrThrow(email);

        AiAnalysisResponse ai = aiResultProvider.analyze(imageUrl);
        AiAnalysisRawResult raw = aiResultProvider.analyzeRaw(imageUrl);

        // ✅ 1️⃣ conditions null 방어 (가장 중요)
        List<AiAnalysisResponse.ConditionResult> conditions =
                Optional.ofNullable(ai.getConditions()).orElse(List.of());

        if (conditions.isEmpty()) {
            throw new IllegalStateException("AI 분석 결과 conditions가 비어있습니다.");
        }

        Map<ConditionType, ConditionLevel> levelMap = new HashMap<>();
        for (AiAnalysisResponse.ConditionResult cr : conditions) {
            levelMap.put(
                    cr.getConditionType(),
                    levelCalculator.calc(cr.getConditionType(), cr.getValue())
            );
        }

        String summary = summaryGenerator.summary(
                ai.getSkinTypeCode(),
                levelMap
        );

        SkinAnalysis analysis = new SkinAnalysis(
                user,
                ai.getSkinTypeCode(),
                summary,
                imageUrl
        );

        SkinAnalysis saved = skinAnalysisRepository.save(analysis);

        List<ConditionView> conditionViews = new ArrayList<>();
        for (AiAnalysisResponse.ConditionResult cr : conditions) {

            ConditionLevel level = levelMap.get(cr.getConditionType());

            SkinAnalysisCondition cond = new SkinAnalysisCondition(
                    saved,
                    cr.getConditionType(),
                    cr.getValue(),
                    level,
                    summaryGenerator.description(cr.getConditionType(), level)
            );
            conditionRepository.save(cond);

            conditionViews.add(
                    ConditionView.builder()
                            .type(cr.getConditionType().name())
                            .name(summaryGenerator.conditionNameToKorean(cr.getConditionType()))
                            .level(summaryGenerator.levelToKorean(level))
                            .value(cr.getValue())
                            .description(cond.getDescription())
                            .build()
            );
        }

        // ✅ 2️⃣ raw / metrics null 방어
        List<RegionView> regions =
                (raw == null || raw.getMetrics() == null)
                        ? List.of()
                        : regionMetricMapper.map(raw.getMetrics());

        return SkinAnalysisResultResponse.builder()
                .analysisId(saved.getId())
                .skinType(summaryGenerator.skinTypeToKorean(ai.getSkinTypeCode()))
                .summary(summary)
                .conditions(conditionViews)
                .regions(regions)
                .build();
    }

    /**
     * ✅ 캘린더 히스토리 조회
     */
    @Transactional(readOnly = true)
    public List<AnalysisHistoryItemResponse> getMyHistory(String email) {

        User user = userService.getByEmailOrThrow(email);

        return skinAnalysisRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(a -> AnalysisHistoryItemResponse.builder()
                        .analysisId(a.getId())
                        .date(a.getCreatedAt().toLocalDate().toString())
                        .skinType(summaryGenerator.skinTypeToKorean(a.getSkinTypeCode()))
                        .summary(a.getSummaryText())
                        .build()
                )
                .toList();
    }

    /**
     * ✅ 단건 분석 상세 조회
     */
    @Transactional(readOnly = true)
    public SkinAnalysisResultResponse getAnalysisDetail(Long analysisId, String email) {

        SkinAnalysis analysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("분석 결과가 존재하지 않습니다."));

        if (!analysis.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        List<ConditionView> views =
                conditionRepository.findByAnalysisId(analysisId)
                        .stream()
                        .map(c -> ConditionView.builder()
                                .type(c.getConditionType().name())
                                .name(summaryGenerator.conditionNameToKorean(c.getConditionType()))
                                .level(summaryGenerator.levelToKorean(c.getConditionLevel()))
                                .value(c.getConditionValue())
                                .description(c.getDescription())
                                .build()
                        )
                        .toList();

        return SkinAnalysisResultResponse.builder()
                .analysisId(analysis.getId())
                .skinType(summaryGenerator.skinTypeToKorean(analysis.getSkinTypeCode()))
                .summary(analysis.getSummaryText())
                .conditions(views)
                .regions(List.of()) // ✅ null 절대 금지
                .build();
    }

    /**
     * ✅ AI 인사이트 조회
     */
    @Transactional(readOnly = true)
    public AnalysisInsightResponse getInsight(Long analysisId, String email) {

        SkinAnalysis analysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("분석 결과가 존재하지 않습니다."));

        if (!analysis.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        List<SkinAnalysisCondition> conds =
                conditionRepository.findByAnalysisId(analysisId);

        String prompt = insightPromptBuilder.build(analysis, conds);
        String insight = deepSeekInsightClient.generateInsight(prompt);

        if (insight == null || insight.isBlank()) {
            insight = analysis.getSummaryText();
        }

        return new AnalysisInsightResponse(
                analysis.getCreatedAt().toLocalDate().toString(),
                summaryGenerator.skinTypeToKorean(analysis.getSkinTypeCode()),
                analysis.getSummaryText(),
                conds.stream()
                        .map(c -> new AnalysisInsightResponse.ConditionInsight(
                                c.getConditionType().name(),
                                c.getConditionValue(),
                                c.getConditionLevel().name()
                        ))
                        .toList(),
                insight
        );
    }
}
