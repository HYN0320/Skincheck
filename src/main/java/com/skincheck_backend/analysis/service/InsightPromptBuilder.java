package com.skincheck_backend.analysis.service;

import com.skincheck_backend.analysis.entity.SkinAnalysis;
import com.skincheck_backend.analysis.entity.SkinAnalysisCondition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InsightPromptBuilder {

    public String build(
            SkinAnalysis analysis,
            List<SkinAnalysisCondition> conditions
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
너는 피부 관리 앱의 AI 인사이트 생성기야.
아래 피부 분석 결과를 바탕으로 오늘 피부 상태를 설명해줘.

조건:
- 한국어
- 존댓말
- 과장 금지
- 의학적 진단처럼 말하지 말 것
- 관리 팁 2~3개
- 5~7문장 이내

[피부 분석 결과]
""");

        sb.append("- 피부 타입: ")
                .append(analysis.getSkinTypeCode().name())
                .append("\n");

        for (SkinAnalysisCondition c : conditions) {
            sb.append("- ")
                    .append(c.getConditionType().name())
                    .append(": ")
                    .append(c.getConditionValue())
                    .append(" (")
                    .append(c.getConditionLevel().name())
                    .append(")\n");
        }

        return sb.toString();
    }
}
