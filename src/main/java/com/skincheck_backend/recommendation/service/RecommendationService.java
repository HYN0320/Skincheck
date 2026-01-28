package com.skincheck_backend.recommendation.service;

import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.external.NaverCosmeticClient;
import com.skincheck_backend.external.dto.NaverShoppingItem;
import com.skincheck_backend.recommendation.dto.CosmeticDto;
import com.skincheck_backend.recommendation.dto.HomeRecommendationSection;
import com.skincheck_backend.recommendation.entity.Cosmetic;
import com.skincheck_backend.recommendation.repository.CosmeticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CosmeticRepository cosmeticRepository;
    private final NaverCosmeticClient naverClient;

    /**
     * 홈 화면용 추천
     */
    public HomeRecommendationSection recommend(ConditionType type, int score) {

        // 1️⃣ DB 기반 추천
        List<CosmeticDto> dbCosmetics =
                cosmeticRepository.findTop5ByCategory(type)
                        .stream()
                        .map(CosmeticDto::from)
                        .toList();

        // 2️⃣ 네이버 실시간 추천 (🔥 수정 포인트)
        List<CosmeticDto> realtimeCosmetics =
                naverClient.search(type.getKoreanName() + " 화장품")
                        .getItems()
                        .stream()
                        .map(this::fromNaver)
                        .toList();

        // 3️⃣ 병합 (DB 우선 → 네이버 보강)
        List<CosmeticDto> merged =
                Stream.concat(dbCosmetics.stream(), realtimeCosmetics.stream())
                        .distinct()
                        .limit(10)
                        .toList();

        return new HomeRecommendationSection(type, score, merged);
    }

    /**
     * 네이버 쇼핑 아이템 → CosmeticDto 변환
     */
    private CosmeticDto fromNaver(NaverShoppingItem item) {
        return new CosmeticDto(
                cleanHtml(item.getTitle()),
                item.getBrand(),
                item.getImage(),
                item.getLink()
        );
    }

    /**
     * 네이버 title의 HTML 태그 제거
     */
    private String cleanHtml(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]*>", "");
    }
}
