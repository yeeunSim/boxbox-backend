package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {
}
