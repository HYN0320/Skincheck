package com.skincheck_backend.User.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    /* ===== 이메일 인증 ===== */

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(length = 100)
    private String emailVerifyToken;

    @Column
    private LocalDateTime emailVerifyExpiresAt;

    public void createEmailVerifyToken() {
        this.emailVerifyToken = UUID.randomUUID().toString();
        this.emailVerifyExpiresAt = LocalDateTime.now().plusHours(24);
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerifyToken = null;
        this.emailVerifyExpiresAt = null;
    }

    public boolean isEmailVerifyExpired() {
        return emailVerifyExpiresAt != null &&
                emailVerifyExpiresAt.isBefore(LocalDateTime.now());
    }
}
