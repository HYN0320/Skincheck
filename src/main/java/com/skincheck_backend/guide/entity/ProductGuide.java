package com.skincheck_backend.guide.entity;

import com.skincheck_backend.common.enumtype.ConditionType;
import com.skincheck_backend.common.enumtype.SkinTypeCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "product_guide")
public class ProductGuide {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SkinTypeCode skinTypeCode;

    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    private Integer minValue;
    private Integer maxValue;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 255)
    private String content;
}
