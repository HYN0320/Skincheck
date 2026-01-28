package com.skincheck_backend.common.enumtype;

import com.skincheck_backend.analysis.dto.AiAnalysisResponse;

public enum ConditionType {

    MOISTURE("수분"),
    ELASTICITY("탄력"),
    PORE("모공"),
    PIGMENTATION("톤");

    private final String koreanName;

    ConditionType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
    private int getValue(
            AiAnalysisResponse ai,
            ConditionType type
    ) {
        return ai.getConditions().stream()
                .filter(c -> c.getConditionType() == type)
                .map(AiAnalysisResponse.ConditionResult::getValue)
                .findFirst()
                .orElse(0);
    }

}
