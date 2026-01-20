package com.skincheck_backend.analysis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class AiAnalysisRawResult {

    /**
     * ex)
     * MOISTURE_FOREHEAD -> 22
     * PORE_LEFTCHEEK -> 72
     */
    private Map<String, Integer> metrics;
}
