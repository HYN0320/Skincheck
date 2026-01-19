package com.skincheck_backend.analysis.repository;


import com.skincheck_backend.analysis.entity.SkinAnalysis;
import com.skincheck_backend.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkinAnalysisRepository extends JpaRepository<SkinAnalysis, Long> {
    List<SkinAnalysis> findByUserOrderByCreatedAtDesc(User user);
}
