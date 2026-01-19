package com.skincheck_backend.User.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profile")
@Getter
@NoArgsConstructor
public class UserProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String gender;
    private Integer birthYear;

    // 🔥 이 생성자 추가
    public UserProfile(User user, String gender, Integer birthYear) {
        this.user = user;
        this.gender = gender;
        this.birthYear = birthYear;
    }
}
