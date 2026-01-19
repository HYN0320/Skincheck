package com.skincheck_backend.User.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String name;

    private LocalDateTime createdAt = LocalDateTime.now();

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
