package com.skincheck_backend.recommendation.repository;

import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.recommendation.entity.Cosmetic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CosmeticRepository extends JpaRepository<Cosmetic, Long> {

    List<Cosmetic> findTop5ByCategory(ConditionType category);
}
