package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
