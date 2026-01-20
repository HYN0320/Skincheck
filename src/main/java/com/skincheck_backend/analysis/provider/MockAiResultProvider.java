package com.skincheck_backend.analysis.provider;

import com.skincheck_backend.analysis.dto.AiAnalysisRawResult;
import com.skincheck_backend.analysis.dto.AiAnalysisResponse;
import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.common.enumtype.SkinTypeCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiResultProvider implements AiResultProvider {

    @Override
    public AiAnalysisResponse analyze(String imageUrl) {
        return new AiAnalysisResponse(
                SkinTypeCode.COMBINATION,
                List.of(
                        new AiAnalysisResponse.ConditionResult(ConditionType.MOISTURE, 28),
                        new AiAnalysisResponse.ConditionResult(ConditionType.PORE, 70),
                        new AiAnalysisResponse.ConditionResult(ConditionType.PIGMENTATION, 12),
                        new AiAnalysisResponse.ConditionResult(ConditionType.ELASTICITY, 45)
                )
        );
    }

    // ⭐ 부위별 raw 결과 (Mock)
    @Override
    public AiAnalysisRawResult analyzeRaw(String imageUrl) {
        AiAnalysisRawResult raw = new AiAnalysisRawResult();

        raw.setMetrics(
                Map.of(
                        "MOISTURE_FOREHEAD", 22,
                        "MOISTURE_LEFTCHEEK", 35,
                        "MOISTURE_RIGHTCHEEK", 30,
                        "PORE_LEFTCHEEK", 72,
                        "PORE_RIGHTCHEEK", 68,
                        "PIGMENTATION_FOREHEAD", 15,
                        "ELASTICITY_CHIN", 40
                )
        );

        return raw;
    }
}
