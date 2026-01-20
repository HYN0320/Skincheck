package com.skincheck_backend.analysis.service;

import com.skincheck_backend.User.service.UserService;
import com.skincheck_backend.analysis.dto.*;
import com.skincheck_backend.analysis.entity.SkinAnalysis;
import com.skincheck_backend.analysis.entity.SkinAnalysisCondition;
import com.skincheck_backend.analysis.provider.AiResultProvider;
import com.skincheck_backend.analysis.repository.SkinAnalysisConditionRepository;
import com.skincheck_backend.analysis.repository.SkinAnalysisRepository;
import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.service.UserService;
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

    // ⭐ 부위별 매퍼
    private final RegionMetricMapper regionMetricMapper;

    /**
     * ✅ 이미지 URL 기반 분석 (Mock / Real 공용)
     */
    @Transactional
    public SkinAnalysisResultResponse analyzeAndSave(
            String email,
            String imageUrl
    ) {
        // ✅ 무조건 로그인 사용자
        User user = userService.getByEmailOrThrow(email);

        AiAnalysisResponse ai = aiResultProvider.analyze(imageUrl);
        AiAnalysisRawResult raw = aiResultProvider.analyzeRaw(imageUrl);

        Map<ConditionType, ConditionLevel> levelMap = new HashMap<>();
        for (AiAnalysisResponse.ConditionResult cr : ai.getConditions()) {
            ConditionLevel level =
                    levelCalculator.calc(cr.getConditionType(), cr.getValue());
            levelMap.put(cr.getConditionType(), level);
        }

        String summary = summaryGenerator.summary(
                ai.getSkinTypeCode(),
                levelMap
        );

        SkinAnalysis analysis = new SkinAnalysis(
                user,                      // ✅ 절대 null 아님
                ai.getSkinTypeCode(),
                summary,
                null,
                imageUrl
        );

        SkinAnalysis saved = skinAnalysisRepository.save(analysis);

        List<ConditionView> views = new ArrayList<>();
        for (AiAnalysisResponse.ConditionResult cr : ai.getConditions()) {
            ConditionLevel level = levelMap.get(cr.getConditionType());

            SkinAnalysisCondition cond = new SkinAnalysisCondition(
                    saved,
                    cr.getConditionType(),
                    cr.getValue(),
                    level,
                    summaryGenerator.description(cr.getConditionType(), level)
            );
            conditionRepository.save(cond);

            views.add(
                    ConditionView.builder()
                            .type(cr.getConditionType().name())
                            .name(summaryGenerator.conditionNameToKorean(cr.getConditionType()))
                            .level(summaryGenerator.levelToKorean(level))
                            .value(cr.getValue())
                            .description(cond.getDescription())
                            .build()
            );
        }

        List<RegionView> regions =
                regionMetricMapper.map(raw.getMetrics());

        return SkinAnalysisResultResponse.builder()
                .analysisId(saved.getId())
                .skinType(summaryGenerator.skinTypeToKorean(ai.getSkinTypeCode()))
                .summary(summary)
                .conditions(views)
                .regions(regions)
                .build();
    }


    /**
     * ✅ 내 분석 히스토리 조회
     */
    @Transactional(readOnly = true)
    public List<SkinAnalysisResultResponse> getMyHistory(String email) {

        User user = userService.getByEmailOrThrow(email);
        List<SkinAnalysis> list =
                skinAnalysisRepository.findByUserOrderByCreatedAtDesc(user);

        List<SkinAnalysisResultResponse> res = new ArrayList<>();

        for (SkinAnalysis a : list) {

            List<SkinAnalysisCondition> conds =
                    conditionRepository.findByAnalysisId(a.getId());

            List<ConditionView> views = new ArrayList<>();
            for (SkinAnalysisCondition c : conds) {
                views.add(
                        ConditionView.builder()
                                .type(c.getConditionType().name())
                                .name(summaryGenerator.conditionNameToKorean(c.getConditionType()))
                                .level(summaryGenerator.levelToKorean(c.getConditionLevel()))
                                .value(c.getConditionValue())
                                .description(c.getDescription())
                                .build()
                );
            }

            res.add(
                    SkinAnalysisResultResponse.builder()
                            .analysisId(a.getId())
                            .skinType(summaryGenerator.skinTypeToKorean(a.getSkinTypeCode()))
                            .summary(a.getSummaryText())
                            .conditions(views)
                            .regions(null) // 히스토리는 부위별 제외
                            .build()
            );
        }
        return res;
    }
}
