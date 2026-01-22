package com.skincheck_backend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentHistoryDto {

    private Long analysisId;
    private String date;        // yyyy-MM-dd
    private String skinType;    // 건성/지성/복합성 등
    private String summary;     // 요약 문구
}
