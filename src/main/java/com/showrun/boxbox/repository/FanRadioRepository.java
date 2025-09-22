package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.FanRadio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FanRadioRepository extends JpaRepository<FanRadio, Long> {
    @Modifying
    @Query("""
           UPDATE FanRadio f 
              SET f.radioDeletedYn = true
            WHERE f.radioSn = :radioSn
              AND f.user.userSn = :userSn
              AND f.radioDeletedYn = false
           """)
    int softDeleteByOwner(@Param("radioSn") Long radioSn, @Param("userSn") Long userSn);
}
