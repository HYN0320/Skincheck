package com.skincheck_backend.analysis.provider;

import com.skincheck_backend.analysis.dto.AiAnalysisResponse;
import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.common.enumtype.SkinTypeCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
