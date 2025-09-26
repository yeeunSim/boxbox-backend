package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.FanRadio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface FanRadioRepository extends JpaRepository<FanRadio, Long> {

    Optional<FanRadio> findByRadioSnAndUser_UserSn(Long radioSn, Long userSn);

    long countByUser_UserSnAndRadioDeletedYnFalse(Long userSn);

    Optional<FanRadio> findByRadioSnAndUser_UserSnAndRadioDeletedYnFalse(Long radioSn, Long userSn);

    @Modifying
    @Query("""
           UPDATE FanRadio f
              SET f.radioDeletedYn = true
            WHERE f.radioSn = :radioSn
              AND f.user.userSn = :userSn
              AND f.radioDeletedYn = false
           """)
    int softDeleteByOwner(@Param("radioSn") Long radioSn, @Param("userSn") Long userSn);

    @Query("""
        SELECT f FROM FanRadio f
         WHERE f.radioDeletedYn = false
         ORDER BY f.radioLikeCount DESC, f.radioSn DESC
    """)
    Slice<FanRadio> findPopular(Pageable pageable);

    @Query("""
        SELECT f FROM FanRadio f
         WHERE f.radioDeletedYn = false
         ORDER BY f.radioCreatedAt DESC, f.radioSn DESC
    """)
    Slice<FanRadio> findLatest(Pageable pageable);

    // contains 검색(대소문자 무시는 컬럼 콜레이션이 처리)
    @Query("""
        SELECT f FROM FanRadio f
         WHERE f.radioDeletedYn = false
           AND f.radioNickname LIKE CONCAT('%', :nickname, '%')
         ORDER BY f.radioLikeCount DESC, f.radioSn DESC
    """)
    Slice<FanRadio> searchPopular(@Param("nickname") String nickname, Pageable pageable);

    @Query("""
        SELECT f FROM FanRadio f
         WHERE f.radioDeletedYn = false
           AND f.radioNickname LIKE CONCAT('%', :nickname, '%')
         ORDER BY f.radioCreatedAt DESC, f.radioSn DESC
    """)
    Slice<FanRadio> searchLatest(@Param("nickname") String nickname, Pageable pageable);
}