package com.skincheck_backend.common.enumtype;

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
}
