package com.skincheck_backend.analysis.service;

import com.skincheck_backend.analysis.dto.RegionConditionView;
import com.skincheck_backend.analysis.dto.RegionView;
import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RegionMetricMapper {

    private final ConditionLevelCalculator levelCalculator;

    /**
     * AI raw metric Map → RegionView 리스트 변환
     */
    public List<RegionView> map(Map<String, Integer> metrics) {

        /**
         * bucket 구조
         *
         * region (leftCheek)
         *   └─ ConditionType (ELASTICITY)
         *        └─ [29, 37, 34, 32]
         */
        Map<String, Map<ConditionType, List<Integer>>> bucket = new HashMap<>();

        // 1️⃣ raw metric 분류
        metrics.forEach((key, value) -> {

            // 예: ELASTICITY_LEFTCHEEK_Q0
            String[] parts = key.split("_");
            if (parts.length < 2) return;

            String typeStr = parts[0];
            String regionRaw = parts[1];

            ConditionType conditionType;
            try {
                conditionType = ConditionType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                // enum에 없는 타입은 무시
                return;
            }

            String region = normalize(regionRaw);

            // chin 제거 / 알 수 없는 region 방어
            if (!isSupportedRegion(region)) return;

            bucket
                    .computeIfAbsent(region, r -> new HashMap<>())
                    .computeIfAbsent(conditionType, t -> new ArrayList<>())
                    .add(value);
        });

        // 2️⃣ bucket → RegionView 변환
        List<RegionView> regions = new ArrayList<>();

        for (var regionEntry : bucket.entrySet()) {

            String region = regionEntry.getKey();
            List<RegionConditionView> conditionViews = new ArrayList<>();

            for (var condEntry : regionEntry.getValue().entrySet()) {

                ConditionType type = condEntry.getKey();
                List<Integer> values = condEntry.getValue();

                // ⭐ 대표값 계산 (평균)
                int avgValue = (int) values.stream()
                        .mapToInt(v -> v)
                        .average()
                        .orElse(0);

                // ⭐ level 계산
                ConditionLevel level =
                        levelCalculator.calc(type, avgValue);

                conditionViews.add(
                        RegionConditionView.builder()
                                .type(type.name())       // MOISTURE / ELASTICITY ...
                                .value(avgValue)
                                .level(level.name())     // GOOD / NORMAL / LOW / BAD
                                .build()
                );
            }

            regions.add(
                    RegionView.builder()
                            .region(region)              // forehead / leftEye / ...
                            .conditions(conditionViews)
                            .build()
            );
        }

        return regions;
    }

    /**
     * AI raw region → 프론트 기준 region 매핑
     */
    private String normalize(String raw) {
        return switch (raw) {
            case "FOREHEAD" -> "forehead";
            case "LEFTCHEEK" -> "leftCheek";
            case "RIGHTCHEEK" -> "rightCheek";

            case "LEFTEYE" -> "leftEye";
            case "RIGHTEYE" -> "rightEye";

            case "LIP", "MOUTH" -> "lip";

            // chin 제거 (더 이상 사용 안 함)
            default -> raw.toLowerCase();
        };
    }

    /**
     * 프론트에서 사용하는 region만 허용
     */
    private boolean isSupportedRegion(String region) {
        return Set.of(
                "forehead",
                "leftEye",
                "rightEye",
                "leftCheek",
                "rightCheek",
                "lip"
        ).contains(region);
    }
}
