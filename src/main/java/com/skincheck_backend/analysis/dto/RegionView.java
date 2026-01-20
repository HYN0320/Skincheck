package com.skincheck_backend.analysis.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RegionView {
    private String region; // forehead, leftCheek ...
    private List<RegionConditionView> conditions;
}
