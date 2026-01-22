package com.skincheck_backend.recommendation.entity;

import com.skincheck_backend.common.enumtype.ConditionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "cosmetic")
public class Cosmetic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String brand;
    private String imageUrl;
    private String link;

    @Enumerated(EnumType.STRING)
    private ConditionType category;
}
