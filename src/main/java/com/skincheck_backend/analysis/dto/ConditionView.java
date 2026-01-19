package com.skincheck_backend.analysis.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConditionView {
    private String type;        // "MOISTURE"
    private String name;        // "수분"
    private String level;       // "주의/보통/좋음/나쁨"
    private int value;          // raw
    private String description; // UX 설명
}
