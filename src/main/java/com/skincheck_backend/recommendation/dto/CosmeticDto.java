package com.skincheck_backend.recommendation.dto;

import com.skincheck_backend.recommendation.entity.Cosmetic;
import lombok.Getter;
@Getter
public class CosmeticDto {

    private Long id;
    private String name;
    private String brand;
    private String imageUrl;
    private String link;

    // ✅ 네이버 / 외부 API용 생성자
    public CosmeticDto(
            String name,
            String brand,
            String imageUrl,
            String link
    ) {
        this.name = name;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.link = link;
    }

    // ✅ DB Entity → DTO
    public static CosmeticDto from(Cosmetic cosmetic) {
        return new CosmeticDto(
                cosmetic.getName(),
                cosmetic.getBrand(),
                cosmetic.getImageUrl(),
                cosmetic.getLink()
        );
    }
}
