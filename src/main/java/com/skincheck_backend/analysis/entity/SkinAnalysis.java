package com.skincheck_backend.analysis.entity;

import com.skincheck_backend.common.enumtype.SkinTypeCode;
import com.skincheck_backend.User.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "skin_analysis")
public class SkinAnalysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SkinTypeCode skinTypeCode;

    @Column(nullable = false, length = 255)
    private String summaryText;

    @Column(length = 20)
    private String riskLevel;

    @Column(length = 255)
    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    public SkinAnalysis(User user, SkinTypeCode skinTypeCode, String summaryText, String riskLevel, String imageUrl) {
        this.user = user;
        this.skinTypeCode = skinTypeCode;
        this.summaryText = summaryText;
        this.riskLevel = riskLevel;
        this.imageUrl = imageUrl;
    }
}
