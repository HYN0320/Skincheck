package com.skincheck_backend.guide.repository;


import com.skincheck_backend.guide.entity.ProductGuide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductGuideRepository extends JpaRepository<ProductGuide, Long> {
}
