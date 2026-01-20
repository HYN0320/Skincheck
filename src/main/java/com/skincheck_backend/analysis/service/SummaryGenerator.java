package com.skincheck_backend.analysis.service;

import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.common.enumtype.SkinTypeCode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SummaryGenerator {

    /* =============================
     * 명칭 변환
     * ============================= */

    public String conditionNameToKorean(ConditionType type) {
        return switch (type) {
            case MOISTURE -> "수분";
            case ELASTICITY -> "탄력";
            case PIGMENTATION -> "색소침착";
            case PORE -> "모공";
        };
    }

    public String levelToKorean(ConditionLevel level) {
        return switch (level) {
            case GOOD -> "좋음";
            case NORMAL -> "보통";
            case LOW -> "부족";
            case BAD -> "나쁨";
        };
    }

    public String skinTypeToKorean(SkinTypeCode code) {
        return switch (code) {
            case NORMAL -> "중성";
            case DRY -> "건성";
            case OILY -> "지성";
            case COMBINATION -> "복합성";
            case SENSITIVE -> "민감성";
        };
    }

    /* =============================
     * 🔥 최종 요약 로직
     * ============================= */

    public String summary(
            SkinTypeCode skinType,
            Map<ConditionType, ConditionLevel> levelMap
    ) {
        ConditionLevel elasticity =
                levelMap.getOrDefault(ConditionType.ELASTICITY, ConditionLevel.NORMAL);

        ConditionLevel moisture =
                levelMap.getOrDefault(ConditionType.MOISTURE, ConditionLevel.NORMAL);

        ConditionLevel pore =
                levelMap.getOrDefault(ConditionType.PORE, ConditionLevel.NORMAL);

        // 1️⃣ 가장 체감 큰 문제부터
        if (elasticity == ConditionLevel.BAD) {
            return "전반적으로 피부 탄력이 부족해 관리가 필요해요.";
        }

        if (moisture == ConditionLevel.BAD) {
            return "전반적으로 피부 수분 관리가 필요해요.";
        }

        // 2️⃣ 컨디션 저하
        if (elasticity == ConditionLevel.LOW || moisture == ConditionLevel.LOW) {
            return "피부 컨디션이 다소 저하된 상태예요.";
        }

        if (pore == ConditionLevel.BAD || pore == ConditionLevel.LOW) {
            return "모공 관리에 조금 더 신경 써주면 좋아요.";
        }

        // 3️⃣ 전반적으로 양호
        return "전반적으로 균형 잡힌 피부 상태예요.";
    }

    /* =============================
     * 상세 설명
     * ============================= */

    public String description(ConditionType type, ConditionLevel level) {
        return conditionNameToKorean(type)
                + " 상태가 "
                + levelToKorean(level)
                + " 편이에요.";
    }
}
