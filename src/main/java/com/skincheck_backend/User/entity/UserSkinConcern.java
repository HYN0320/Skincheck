package com.skincheck_backend.User.entity;

import com.skincheck_backend.common.enumtype.SkinConcernType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_skin_concern")
@Getter
@NoArgsConstructor
public class UserSkinConcern {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private SkinConcernType concern;

    // ✅ 이 생성자 반드시 있어야 함
    public UserSkinConcern(User user, SkinConcernType concern) {
        this.user = user;
        this.concern = concern;
    }
}
