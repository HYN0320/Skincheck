package com.skincheck_backend.analysis.dto;

import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.common.enumtype.SkinTypeCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponse {

    private SkinTypeCode skinTypeCode;
    private List<ConditionResult> conditions;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionResult {
        private ConditionType conditionType;
        private int value;
    }
}
