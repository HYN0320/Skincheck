package com.skincheck_backend.recommendation.service;

import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.repository.UserRepository;
import com.skincheck_backend.analysis.entity.SkinAnalysis;
import com.skincheck_backend.analysis.entity.SkinAnalysisCondition;
import com.skincheck_backend.analysis.repository.SkinAnalysisConditionRepository;
import com.skincheck_backend.analysis.repository.SkinAnalysisRepository;
import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.recommendation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final RecommendationService recommendationService;
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final SkinAnalysisConditionRepository conditionRepository;
    private final UserRepository userRepository;

    public HomeResponse home(String email) {

        // 1️⃣ 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // 2️⃣ 최근 분석 기준 피부 상태 계산
        SkinAnalysis latest =
                skinAnalysisRepository
                        .findByUserOrderByCreatedAtDesc(user)
                        .stream()
                        .findFirst()
                        .orElse(null);

        SkinStatusDto status;

        if (latest == null) {
            // 분석 기록 없을 때
            status = new SkinStatusDto(0, 0, 0, 0);
        } else {
            List<SkinAnalysisCondition> conds =
                    conditionRepository.findByAnalysisId(latest.getId());

            int moisture = 0;
            int elasticity = 0;
            int pore = 0;
            int pigmentation = 0;

            for (SkinAnalysisCondition c : conds) {
                switch (c.getConditionType()) {
                    case MOISTURE -> moisture = c.getConditionValue();
                    case ELASTICITY -> elasticity = c.getConditionValue();
                    case PORE -> pore = c.getConditionValue();
                    case PIGMENTATION -> pigmentation = c.getConditionValue();
                }
            }

            status = new SkinStatusDto(
                    moisture,
                    elasticity,
                    pore,
                    pigmentation
            );
        }

        // 3️⃣ 추천 섹션 (최근 분석 점수 기준)
        List<HomeRecommendationSection> sections =
                List.of(
                        recommendationService.recommend(ConditionType.MOISTURE, status.getMoisture()),
                        recommendationService.recommend(ConditionType.ELASTICITY, status.getElasticity()),
                        recommendationService.recommend(ConditionType.PORE, status.getPore()),
                        recommendationService.recommend(ConditionType.PIGMENTATION, status.getPigmentation())
                );

        // 4️⃣ 최근 분석 히스토리 (최대 3개)
        List<RecentHistoryDto> recentHistories =
                skinAnalysisRepository
                        .findByUserOrderByCreatedAtDesc(user)
                        .stream()
                        .limit(3)
                        .map(a -> new RecentHistoryDto(
                                a.getId(),
                                a.getCreatedAt().toLocalDate().toString(),
                                a.getSkinTypeCode().name(),
                                a.getSummaryText()
                        ))
                        .toList();

        // 5️⃣ 홈 응답
        return HomeResponse.builder()
                .skinStatus(status)
                .recentHistories(recentHistories)
                .recommendations(sections)
                .build();
    }
}
