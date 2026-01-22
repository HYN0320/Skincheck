package com.skincheck_backend.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeResponse {

    private SkinStatusDto skinStatus;

    @Builder.Default
    private List<RecentHistoryDto> recentHistories = List.of();

    @Builder.Default
    private List<HomeRecommendationSection> recommendations = List.of();
}
