package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.Login;
import com.showrun.boxbox.dto.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    Optional<Login> findByLoginEmail(String loginEmail);
}
