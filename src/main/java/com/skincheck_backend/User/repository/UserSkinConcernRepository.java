package com.skincheck_backend.User.repository;

import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.entity.UserSkinConcern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSkinConcernRepository extends JpaRepository<UserSkinConcern, Long> {

    List<UserSkinConcern> findByUser(User user);

    void deleteByUser(User user);
}
