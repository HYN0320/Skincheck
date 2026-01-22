package com.skincheck_backend.analysis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnalysisHistoryItemResponse {

    private Long analysisId;

    // 캘린더용 (yyyy-MM-dd)
    private String date;

    // Insight 미리보기용
    private String skinType;
    private String summary;
}
