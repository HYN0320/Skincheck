package com.skincheck_backend.recommendation.dto;

import com.skincheck_backend.common.enumtype.ConditionType;
import lombok.Getter;

import java.util.List;

@Getter
public class HomeRecommendationSection {

    private String category;
    private String title;
    private String guide;
    private List<CosmeticDto> products;

    public HomeRecommendationSection(
            ConditionType type,
            int score,
            List<CosmeticDto> products
    ) {
        this.category = type.name();
        this.title = type.getKoreanName();
        this.guide = score < 60 ? "집중 케어가 필요해요" : "현재 상태를 유지해보세요";
        this.products = products;
    }
}
