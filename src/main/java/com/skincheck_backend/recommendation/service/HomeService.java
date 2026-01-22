package com.skincheck_backend.recommendation.service;

import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.recommendation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class HomeService {

    private final RecommendationService recommendationService;

    public HomeResponse home(String email) {

        SkinStatusDto status =
                new SkinStatusDto(72, 68, 75, 80);

        List<HomeRecommendationSection> sections =
                List.of(
                        recommendationService.recommend(ConditionType.MOISTURE, 72),
                        recommendationService.recommend(ConditionType.ELASTICITY, 68),
                        recommendationService.recommend(ConditionType.PORE, 75),
                        recommendationService.recommend(ConditionType.PIGMENTATION, 80)
                );

        return HomeResponse.builder()
                .skinStatus(status)
                .recentHistories(List.of())   // 🔥 null ❌, 빈 리스트 ✅
                .recommendations(sections)
                .build();
    }
}
