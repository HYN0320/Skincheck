package com.skincheck_backend.analysis.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SkinAnalysisResultResponse {
    private Long analysisId;
    private String skinType;
    private String summary;
    private List<ConditionView> conditions;
}
