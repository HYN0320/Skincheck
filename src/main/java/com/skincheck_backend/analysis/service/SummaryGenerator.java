package com.skincheck_backend.analysis.service;

import com.skincheck_backend.analysis.entity.SkinAnalysisCondition;
import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.common.enumtype.SkinTypeCode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SummaryGenerator {

    /* =====================================================
       1️⃣ 지표별 문구 풀
     ===================================================== */
    private static final Map<ConditionType, Map<ConditionLevel, List<String>>> MESSAGE_POOL =
            Map.of(
                    ConditionType.MOISTURE, Map.of(
                            ConditionLevel.BAD, List.of(
                                    "피부 수분이 많이 부족해 건조함이 크게 느껴질 수 있어요."
                            ),
                            ConditionLevel.LOW, List.of(
                                    "수분이 다소 부족해 보습 관리가 필요해 보여요."
                            ),
                            ConditionLevel.NORMAL, List.of(
                                    "수분 상태는 비교적 안정적인 편이에요."
                            ),
                            ConditionLevel.GOOD, List.of(
                                    "피부가 촉촉한 상태를 잘 유지하고 있어요."
                            )
                    ),

                    ConditionType.ELASTICITY, Map.of(
                            ConditionLevel.BAD, List.of(
                                    "피부 탄력이 많이 저하된 상태로 관리가 필요해 보여요."
                            ),
                            ConditionLevel.LOW, List.of(
                                    "탄력이 다소 떨어져 있어 관리가 도움이 될 것 같아요."
                            ),
                            ConditionLevel.NORMAL, List.of(
                                    "탄력은 전반적으로 안정적인 상태예요."
                            ),
                            ConditionLevel.GOOD, List.of(
                                    "탄력 있는 피부 상태가 잘 유지되고 있어요."
                            )
                    ),

                    ConditionType.PORE, Map.of(
                            ConditionLevel.BAD, List.of(
                                    "모공이 눈에 띄는 편으로 관리가 필요한 상태예요."
                            ),
                            ConditionLevel.LOW, List.of(
                                    "모공이 다소 늘어진 상태로 보이지만 큰 문제는 아니에요."
                            ),
                            ConditionLevel.NORMAL, List.of(
                                    "모공 상태는 비교적 안정적인 편이에요."
                            ),
                            ConditionLevel.GOOD, List.of(
                                    "모공이 깔끔한 피부 상태를 유지하고 있어요."
                            )
                    ),

                    ConditionType.PIGMENTATION, Map.of(
                            ConditionLevel.BAD, List.of(
                                    "색소 침착이 비교적 뚜렷하게 보이는 상태예요."
                            ),
                            ConditionLevel.LOW, List.of(
                                    "잡티와 색소가 조금 눈에 띄는 편이에요."
                            ),
                            ConditionLevel.NORMAL, List.of(
                                    "피부 톤이 비교적 균일한 상태예요."
                            ),
                            ConditionLevel.GOOD, List.of(
                                    "피부 톤이 맑고 깨끗한 상태를 유지하고 있어요."
                            )
                    )
            );

    /* =====================================================
       2️⃣ 요약 생성 (🔥 핵심)
     ===================================================== */
    public String generate(List<SkinAnalysisCondition> conditions) {

        long badCount = conditions.stream()
                .filter(c -> c.getConditionLevel() == ConditionLevel.BAD)
                .count();

        if (badCount >= 2) {
            return randomPick(List.of(
                    "전반적으로 피부 컨디션 관리가 필요한 상태로 보여요.",
                    "전체적인 피부 균형이 무너져 있어 관리가 필요해 보여요."
            ));
        }

        SkinAnalysisCondition target =
                conditions.stream()
                        .min(Comparator.comparing(c -> c.getConditionLevel().ordinal()))
                        .orElse(null);

        if (target == null) {
            return "피부 상태를 종합적으로 분석했습니다.";
        }

        return description(target.getConditionType(), target.getConditionLevel());
    }

    /* =====================================================
       3️⃣ 지표별 설명 (ConditionView.description)
     ===================================================== */
    public String description(ConditionType type, ConditionLevel level) {
        return randomPick(
                MESSAGE_POOL
                        .getOrDefault(type, Map.of())
                        .getOrDefault(level, List.of(
                                "피부 상태를 종합적으로 분석했습니다."
                        ))
        );
    }

    /* =====================================================
       4️⃣ 한글 변환 유틸
     ===================================================== */
    public String conditionNameToKorean(ConditionType type) {
        return switch (type) {
            case MOISTURE -> "수분";
            case ELASTICITY -> "탄력";
            case PORE -> "모공";
            case PIGMENTATION -> "색소";
        };
    }

    public String levelToKorean(ConditionLevel level) {
        return switch (level) {
            case BAD -> "나쁨";
            case LOW -> "주의";
            case NORMAL -> "보통";
            case GOOD -> "좋음";
        };
    }

    public String skinTypeToKorean(SkinTypeCode code) {
        return switch (code) {
            case DRY -> "건성";
            case OILY -> "지성";
            case COMBINATION -> "복합성";
            case NORMAL -> "중성";
            case SENSITIVE -> "민감성";
        };
    }

    /* =====================================================
       util
     ===================================================== */
    private String randomPick(List<String> list) {
        return list.get(
                ThreadLocalRandom.current().nextInt(list.size())
        );
    }
}
