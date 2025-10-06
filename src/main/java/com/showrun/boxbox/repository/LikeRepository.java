package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface LikeRepository extends JpaRepository<Like, Long> {

    // (radioSn, likeUserSn)로 존재 여부
    boolean existsByFanRadio_radioSnAndLikeUserSn(Long radioSn, Long likeUserSn);

    // (radioSn, likeUserSn)로 삭제 — 영향 행 수 반환
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    long deleteByFanRadio_radioSnAndLikeUserSn(Long radioSn, Long likeUserSn);
}
