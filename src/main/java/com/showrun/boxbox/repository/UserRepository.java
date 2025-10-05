package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin_LoginEmail(String loginEmail);

    boolean existsByUserNickname(String nickname);
}
