package com.skincheck_backend.analysis.service;

import com.skincheck_backend.analysis.dto.AiAnalysisResponse;
import com.skincheck_backend.analysis.dto.ConditionView;
import com.skincheck_backend.analysis.dto.SkinAnalysisResultResponse;
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

    /**
     * ✅ 이미지 URL 기반 분석 (Mock / Real 공용)
     */
    @Transactional
    public SkinAnalysisResultResponse analyzeAndSave(
            String email,
            String imageUrl
    ) {
        // 1️⃣ 사용자 조회
        User user = userService.getByEmailOrThrow(email);

        // 2️⃣ AI 분석
        AiAnalysisResponse ai = aiResultProvider.analyze(imageUrl);

        // 3️⃣ Condition Level 계산
        Map<ConditionType, ConditionLevel> levelMap = new HashMap<>();
        for (AiAnalysisResponse.ConditionResult cr : ai.getConditions()) {
            ConditionLevel level =
                    levelCalculator.calc(cr.getConditionType(), cr.getValue());
            levelMap.put(cr.getConditionType(), level);
        }

        // 4️⃣ Summary 생성
        String summary = summaryGenerator.summary(
                ai.getSkinTypeCode(),
                levelMap
        );

        // 5️⃣ SkinAnalysis 저장
        SkinAnalysis analysis = new SkinAnalysis(
                user,
                ai.getSkinTypeCode(),
                summary,
                null,
                imageUrl
        );
        SkinAnalysis saved = skinAnalysisRepository.save(analysis);

        // 6️⃣ Condition 저장 + View 생성
        List<ConditionView> views = new ArrayList<>();

        for (AiAnalysisResponse.ConditionResult cr : ai.getConditions()) {
            ConditionLevel level = levelMap.get(cr.getConditionType());
            String desc = summaryGenerator.description(
                    cr.getConditionType(),
                    level
            );

            SkinAnalysisCondition cond = new SkinAnalysisCondition(
                    saved,
                    cr.getConditionType(),
                    cr.getValue(),
                    level,
                    desc
            );
            conditionRepository.save(cond);

            views.add(
                    ConditionView.builder()
                            .type(cr.getConditionType().name())
                            .name(summaryGenerator.conditionNameToKorean(cr.getConditionType()))
                            .level(summaryGenerator.levelToKorean(level))
                            .value(cr.getValue())
                            .description(desc)
                            .build()
            );
        }

        // 7️⃣ 응답
        return SkinAnalysisResultResponse.builder()
                .analysisId(saved.getId())
                .skinType(summaryGenerator.skinTypeToKorean(ai.getSkinTypeCode()))
                .summary(summary)
                .conditions(views)
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
                            .build()
            );
        }
        return res;
    }
}
