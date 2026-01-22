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

    private static final int DEFAULT_VALUE = 50;

    public List<RegionView> map(Map<String, Integer> metrics) {

        Map<String, Map<ConditionType, List<Integer>>> bucket = new HashMap<>();

        // 1️⃣ raw metric 분류
        metrics.forEach((key, value) -> {

            String[] parts = key.split("_");
            if (parts.length < 2) return;

            ConditionType type;
            try {
                type = ConditionType.valueOf(parts[0]);
            } catch (Exception e) {
                return;
            }

            String region = normalize(parts[1]);
            if (!isSupportedRegion(region)) return;

            bucket
                    .computeIfAbsent(region, r -> new HashMap<>())
                    .computeIfAbsent(type, t -> new ArrayList<>())
                    .add(value);
        });

        // 2️⃣ 모든 region 강제 생성
        List<String> allRegions = List.of(
                "forehead",
                "leftEye",
                "rightEye",
                "leftCheek",
                "rightCheek",
                "lip"
        );

        List<RegionView> regions = new ArrayList<>();

        for (String region : allRegions) {

            Map<ConditionType, List<Integer>> condMap =
                    bucket.getOrDefault(region, Map.of());

            List<RegionConditionView> conditionViews = new ArrayList<>();

            for (ConditionType type : ConditionType.values()) {

                List<Integer> values = condMap.get(type);

                int value = (values == null || values.isEmpty())
                        ? DEFAULT_VALUE
                        : (int) values.stream().mapToInt(v -> v).average().orElse(DEFAULT_VALUE);

                ConditionLevel level = levelCalculator.calc(type, value);

                conditionViews.add(
                        RegionConditionView.builder()
                                .type(type.name())
                                .value(value)
                                .level(level.name())
                                .build()
                );
            }

            regions.add(
                    RegionView.builder()
                            .region(region)
                            .conditions(conditionViews)
                            .build()
            );
        }

        return regions;
    }

    private String normalize(String raw) {
        return switch (raw) {
            case "FOREHEAD" -> "forehead";
            case "LEFTCHEEK" -> "leftCheek";
            case "RIGHTCHEEK" -> "rightCheek";
            case "LEFTEYE" -> "leftEye";
            case "RIGHTEYE" -> "rightEye";
            case "LIP", "MOUTH", "CHIN" -> "lip"; // 🔥 핵심
            default -> raw.toLowerCase();
        };
    }

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
