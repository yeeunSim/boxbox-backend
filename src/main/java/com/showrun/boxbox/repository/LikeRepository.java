package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("select case when count(l)>0 then true else false end " +
            "from Like l where l.fanRadio.radioSn = :radioSn and l.likeUserSn = :likeUserSn")
    boolean exists(@Param("radioSn") Long radioSn, @Param("likeUserSn") Long likeUserSn);

    @Query("select count(l) from Like l where l.fanRadio.radioSn = :radioSn")
    long countByRadio(@Param("radioSn") Long radioSn);

    @Query("select l from Like l where l.fanRadio.radioSn = :radioSn and l.likeUserSn = :likeUserSn")
    Optional<Like> findOne(@Param("radioSn") Long radioSn, @Param("likeUserSn") Long likeUserSn);
}
