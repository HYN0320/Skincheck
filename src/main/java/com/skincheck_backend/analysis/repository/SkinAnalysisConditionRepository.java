package com.skincheck_backend.analysis.repository;

import com.skincheck_backend.analysis.entity.SkinAnalysisCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkinAnalysisConditionRepository
        extends JpaRepository<SkinAnalysisCondition, Long> {

    List<SkinAnalysisCondition> findByAnalysisId(Long analysisId);
}
