package com.skincheck_backend.analysis.repository;

import com.skincheck_backend.analysis.entity.SkinAnalysis;
import com.skincheck_backend.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkinAnalysisRepository extends JpaRepository<SkinAnalysis, Long> {

    // ✅ 사용자별 분석 결과 최신순 조회 (캘린더 / 홈 / 히스토리용)
    List<SkinAnalysis> findByUserOrderByCreatedAtDesc(User user);
}
