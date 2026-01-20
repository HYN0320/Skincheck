package com.skincheck_backend.analysis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionConditionView {
    private String type;   // MOISTURE, PORES, WRINKLE
    private int value;     // 59
    private String level;  // 좋음 / 보통 / 나쁨
}
