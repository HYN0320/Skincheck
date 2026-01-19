package com.skincheck_backend.User.repository;

import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);

    Optional<UserProfile> findByUserId(Long userId);
}
