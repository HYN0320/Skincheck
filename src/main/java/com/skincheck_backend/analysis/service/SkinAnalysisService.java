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
import com.skincheck_backend.recommendation.dto.CosmeticDto;
import com.skincheck_backend.recommendation.service.RecommendationService;
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
    private final RecommendationService recommendationService;
    /**
     * ✅ 이미지 업로드 후 분석 + 저장
     */
    @Transactional
    public SkinAnalysisResultResponse analyzeAndSave(String email, String imageUrl) {

        User user = userService.getByEmailOrThrow(email);

        AiAnalysisResponse ai = aiResultProvider.analyze(imageUrl);
        AiAnalysisRawResult raw = aiResultProvider.analyzeRaw(imageUrl);

        // 1️⃣ conditions null 방어
        List<AiAnalysisResponse.ConditionResult> results =
                Optional.ofNullable(ai.getConditions()).orElse(List.of());

        if (results.isEmpty()) {
            throw new IllegalStateException("AI 분석 결과 conditions가 비어있습니다.");
        }

        // 2️⃣ SkinAnalysis 먼저 저장 (summary는 임시)
        SkinAnalysis analysis = new SkinAnalysis(
                user,
                ai.getSkinTypeCode(),
                "", // summary는 나중에
                imageUrl
        );
        SkinAnalysis savedAnalysis = skinAnalysisRepository.save(analysis);

        // 3️⃣ Condition 저장
        List<SkinAnalysisCondition> savedConditions = new ArrayList<>();
        List<ConditionView> conditionViews = new ArrayList<>();

        for (AiAnalysisResponse.ConditionResult cr : results) {

            ConditionLevel level =
                    levelCalculator.calc(cr.getConditionType(), cr.getValue());

            SkinAnalysisCondition condition = new SkinAnalysisCondition(
                    savedAnalysis,
                    cr.getConditionType(),
                    cr.getValue(),
                    level,
                    null // description 나중에
            );

            SkinAnalysisCondition savedCondition =
                    conditionRepository.save(condition);

            savedConditions.add(savedCondition);
        }

        // 4️⃣ 🔥 summary 생성 (핵심 변경)
        String summary = summaryGenerator.generate(savedConditions);

        // 5️⃣ summary 업데이트
        savedAnalysis.updateSummary(summary);

        // 6️⃣ description + ConditionView 구성
        for (SkinAnalysisCondition cond : savedConditions) {

            String description =
                    summaryGenerator.description(
                            cond.getConditionType(),
                            cond.getConditionLevel()
                    );

            cond.updateDescription(description);

            conditionViews.add(
                    ConditionView.builder()
                            .type(cond.getConditionType().name())
                            .name(summaryGenerator.conditionNameToKorean(cond.getConditionType()))
                            .level(summaryGenerator.levelToKorean(cond.getConditionLevel()))
                            .value(cond.getConditionValue())
                            .description(description)
                            .build()
            );
        }

        // 7️⃣ region metrics
        List<RegionView> regions =
                (raw == null || raw.getMetrics() == null)
                        ? List.of()
                        : regionMetricMapper.map(raw.getMetrics());

        return SkinAnalysisResultResponse.builder()
                .analysisId(savedAnalysis.getId())
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
                .regions(List.of())
                .build();
    }

    /**
     * ✅ AI 인사이트 조회
     */
    /**
     * ✅ AI 인사이트 조회 + 화장품 추천
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

        // 1️⃣ AI 인사이트 생성
        String prompt = insightPromptBuilder.build(analysis, conds);
        String insight = deepSeekInsightClient.generateInsight(prompt);

        if (insight == null || insight.isBlank()) {
            insight = analysis.getSummaryText();
        }

        // 2️⃣ 가장 안 좋은 Condition 하나 선택
        SkinAnalysisCondition worstCondition =
                conds.stream()
                        .min(Comparator.comparingInt(SkinAnalysisCondition::getConditionValue))
                        .orElse(null);

        // 3️⃣ 화장품 추천 (이미지 포함)
        List<CosmeticDto> recommendedProducts =
                (worstCondition == null)
                        ? List.of()
                        : recommendationService
                        .recommend(
                                worstCondition.getConditionType(),
                                worstCondition.getConditionValue()
                        )
                        .getProducts();

        // 4️⃣ 최종 응답
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
                insight,
                recommendedProducts // 🔥 추가된 부분
        );
    }

}
