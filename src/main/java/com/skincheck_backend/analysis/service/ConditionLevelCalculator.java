package com.skincheck_backend.analysis.service;

import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import org.springframework.stereotype.Component;

@Component
public class ConditionLevelCalculator {

    public ConditionLevel calc(ConditionType type, int value) {

        if (type == null) {
            throw new IllegalArgumentException("ConditionType is null");
        }

        return switch (type) {

            /**
             * 💧 수분 (생활 영향 큼, 기존보다 살짝 완화)
             */
            case MOISTURE -> {
                if (value < 45) yield ConditionLevel.BAD;
                else if (value < 55) yield ConditionLevel.LOW;
                else if (value < 66) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            /**
             * 🧬 탄력 (모델 분포 기준 보정, 가장 중요)
             */
            case ELASTICITY -> {
                if (value < 40) yield ConditionLevel.BAD;
                else if (value < 50) yield ConditionLevel.LOW;
                else if (value < 60) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            /**
             * 🕳 모공 (값이 높을수록 나쁨)
             */
            case PORE -> {
                if (value >= 80) yield ConditionLevel.BAD;
                else if (value >= 60) yield ConditionLevel.LOW;
                else if (value >= 40) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }

            /**
             * 🎨 색소침착 (보수적으로 유지)
             */
            case PIGMENTATION -> {
                if (value >= 80) yield ConditionLevel.BAD;
                else if (value >= 60) yield ConditionLevel.LOW;
                else if (value >= 30) yield ConditionLevel.NORMAL;
                else yield ConditionLevel.GOOD;
            }
        };
    }
}
