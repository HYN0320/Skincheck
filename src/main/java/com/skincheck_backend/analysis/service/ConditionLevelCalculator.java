package com.skincheck_backend.analysis.service;

import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import org.springframework.stereotype.Component;

@Component
public class ConditionLevelCalculator {

    public ConditionLevel calc(ConditionType type, int value) {

        return switch (type) {

            case MOISTURE -> {
                if (value < 45) yield ConditionLevel.BAD;
                else if (value < 55) yield ConditionLevel.LOW;
                else if (value < 70) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            case ELASTICITY -> {
                if (value < 35) yield ConditionLevel.BAD;
                else if (value < 45) yield ConditionLevel.LOW;
                else if (value < 65) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            case PORE -> {
                if (value >= 80) yield ConditionLevel.BAD;
                else if (value >= 60) yield ConditionLevel.LOW;
                else if (value >= 40) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            case PIGMENTATION -> {
                if (value >= 80) yield ConditionLevel.BAD;
                else if (value >= 60) yield ConditionLevel.LOW;
                else if (value >= 35) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }
        };
    }
}
