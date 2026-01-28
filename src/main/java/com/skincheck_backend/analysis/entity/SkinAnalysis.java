package com.skincheck_backend.analysis.entity;

import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.common.enumtype.SkinTypeCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "skin_analysis")
public class SkinAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkinTypeCode skinTypeCode;

    @Column(nullable = false, length = 255)
    private String summaryText;

    @Column(length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SkinAnalysis(
            User user,
            SkinTypeCode skinTypeCode,
            String summaryText,
            String imageUrl
    ) {
        this.user = user;
        this.skinTypeCode = skinTypeCode;
        this.summaryText = summaryText;
        this.imageUrl = imageUrl;
    }

    // ✅ 추가 (핵심)
    public void updateSummary(String summaryText) {
        this.summaryText = summaryText;
    }
}
