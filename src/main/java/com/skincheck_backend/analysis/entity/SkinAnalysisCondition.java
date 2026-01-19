package com.skincheck_backend.analysis.entity;

import com.skincheck_backend.common.enumtype.ConditionLevel;
import com.skincheck_backend.common.enumtype.ConditionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "skin_analysis_condition")
public class SkinAnalysisCondition {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="analysis_id", nullable = false)
    private SkinAnalysis analysis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConditionType conditionType;

    @Column(nullable = false)
    private int conditionValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConditionLevel conditionLevel;

    @Column(length = 255)
    private String description;

    public SkinAnalysisCondition(SkinAnalysis analysis, ConditionType type, int value, ConditionLevel level, String description) {
        this.analysis = analysis;
        this.conditionType = type;
        this.conditionValue = value;
        this.conditionLevel = level;
        this.description = description;
    }
}
