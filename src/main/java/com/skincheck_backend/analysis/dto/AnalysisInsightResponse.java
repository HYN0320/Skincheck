package com.skincheck_backend.analysis.dto;

import java.util.List;

public class AnalysisInsightResponse {

    private String date;
    private String skinType;
    private String summary;
    private List<ConditionInsight> conditions;
    private String insight;

    public AnalysisInsightResponse(
            String date,
            String skinType,
            String summary,
            List<ConditionInsight> conditions,
            String insight
    ) {
        this.date = date;
        this.skinType = skinType;
        this.summary = summary;
        this.conditions = conditions;
        this.insight = insight;
    }

    public String getDate() { return date; }
    public String getSkinType() { return skinType; }
    public String getSummary() { return summary; }
    public List<ConditionInsight> getConditions() { return conditions; }
    public String getInsight() { return insight; }

    public static class ConditionInsight {
        private String type;
        private int value;
        private String level;

        public ConditionInsight(String type, int value, String level) {
            this.type = type;
            this.value = value;
            this.level = level;
        }

        public String getType() { return type; }
        public int getValue() { return value; }
        public String getLevel() { return level; }
    }
}
