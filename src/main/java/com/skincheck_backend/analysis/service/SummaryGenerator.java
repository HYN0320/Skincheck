package com.skincheck_backend.analysis.service;

import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.common.enumtype.SkinTypeCode;
import com.skincheck_backend.common.enumtype.ConditionLevel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SummaryGenerator {

    public String skinTypeToKorean(SkinTypeCode code) {
        return switch (code) {
            case NORMAL -> "중성";
            case DRY -> "건성";
            case OILY -> "지성";
            case COMBINATION -> "복합성";
            case SENSITIVE -> "민감성";
        };
    }

    public String conditionNameToKorean(ConditionType type) {
        return switch (type) {
            case MOISTURE -> "수분";
            case ELASTICITY -> "탄력";
            case PIGMENTATION -> "색소침착";
            case PORE -> "모공";
            case ACNE -> "트러블";
        };
    }

    public String levelToKorean(ConditionLevel level) {
        return switch (level) {
            case GOOD -> "좋음";
            case NORMAL -> "보통";
            case LOW -> "주의";
            case BAD -> "나쁨";
        };
    }

    public String description(ConditionType type, ConditionLevel level) {
        // 서비스 문장(UX) - 나중에 더 자연스럽게 개선 가능
        return switch (type) {
            case MOISTURE -> switch (level) {
                case LOW -> "수분이 부족한 상태입니다. 보습 위주 관리가 필요합니다.";
                case NORMAL -> "수분 상태가 보통입니다. 꾸준한 보습이 도움이 됩니다.";
                case GOOD -> "수분 상태가 양호합니다. 현재 루틴을 유지해보세요.";
                case BAD -> "수분 상태가 좋지 않습니다. 자극을 줄이고 보습을 강화하세요.";
            };
            case PORE -> switch (level) {
                case BAD -> "모공이 눈에 띄는 상태입니다. 과도한 유분/각질 관리가 필요합니다.";
                case NORMAL -> "모공 상태가 보통입니다. 클렌징과 보습 밸런스를 유지해보세요.";
                case GOOD -> "모공 상태가 양호합니다.";
                case LOW -> "모공 관련 주의가 필요합니다.";
            };
            case PIGMENTATION -> switch (level) {
                case BAD -> "색소침착이 두드러질 수 있습니다. 자외선 차단 관리가 필요합니다.";
                case NORMAL -> "색소침착 상태가 보통입니다. 자외선 차단을 꾸준히 해주세요.";
                case GOOD -> "색소침착 상태가 양호합니다.";
                case LOW -> "색소침착 주의가 필요합니다.";
            };
            case ELASTICITY -> switch (level) {
                case BAD -> "탄력이 낮을 수 있습니다. 보습과 탄력 케어를 고려해보세요.";
                case NORMAL -> "탄력 상태가 보통입니다. 꾸준한 관리가 도움이 됩니다.";
                case GOOD -> "탄력 상태가 양호합니다.";
                case LOW -> "탄력 저하 주의가 필요합니다.";
            };
            case ACNE -> switch (level) {
                case BAD -> "트러블이 심할 수 있습니다. 자극을 줄이고 진정 케어를 권장합니다.";
                case NORMAL -> "트러블 상태가 보통입니다. 수분/유분 밸런스 관리가 필요합니다.";
                case GOOD -> "트러블 상태가 양호합니다.";
                case LOW -> "트러블 주의가 필요합니다.";
            };
        };
    }

    public String summary(SkinTypeCode skinType, Map<ConditionType, ConditionLevel> levelMap) {
        // 간단 요약: 가장 나쁜 1~2개 포인트 기반
        long badCount = levelMap.values().stream().filter(l -> l == ConditionLevel.BAD || l == ConditionLevel.LOW).count();
        String skinK = skinTypeToKorean(skinType);

        if (badCount == 0) {
            return "현재 피부는 전반적으로 양호합니다. (" + skinK + ")";
        }
        if (levelMap.getOrDefault(ConditionType.MOISTURE, ConditionLevel.NORMAL) == ConditionLevel.LOW) {
            return "수분 관리가 필요한 상태입니다. (" + skinK + ")";
        }
        if (levelMap.getOrDefault(ConditionType.PORE, ConditionLevel.NORMAL) == ConditionLevel.BAD) {
            return "모공 관리가 필요한 상태입니다. (" + skinK + ")";
        }
        return "피부 관리가 필요한 항목이 있습니다. (" + skinK + ")";
    }
}
