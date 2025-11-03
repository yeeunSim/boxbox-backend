package com.showrun.boxbox.repository;

import com.showrun.boxbox.domain.*;
import com.showrun.boxbox.dto.fanradio.DriverNumberProjection;
import com.showrun.boxbox.dto.fanradio.FanRadioDetailProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class FanRadioRepositoryTest {

    @Autowired FanRadioRepository fanRadioRepository;
    @Autowired TestEntityManager em;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        Login login1 = Login.create("loginEmail1", "pw1", "tokenValue1", "salt1");
        Login login2 = Login.create("loginEmail2", "pw2", "tokenValue2", "salt2");

        user1 = User.create(login1, "nickname1", LocalDate.of(2000, 12, 21), Gender.F, true, true, false, Status.ACTIVE, true);
        user2 = User.create(login2, "nickname2", LocalDate.of(2000, 12, 22), Gender.F, true, true, false, Status.ACTIVE, true);

        em.persist(user1);
        em.persist(user2);

        em.flush(); em.clear();
    }

    @Test
    @DisplayName("드라이버 넘버가 들어간 게시물 리스트 조회")
    void getDriverNumberListTest() {
        //given
        FanRadio f1 = FanRadio.create(user1, user1.getUserNickname(), "KOR1", "ENG1", 22, false);
        FanRadio f2 = FanRadio.create(user1, user1.getUserNickname(), "KOR2", "ENG2", 20, false);
        FanRadio f3 = FanRadio.create(user1, user1.getUserNickname(), "KOR3", "ENG3", 120, false);
        FanRadio f4 = FanRadio.create(user2, user2.getUserNickname(), "KOR4", "ENG4", 77, false);
        FanRadio f5 = FanRadio.create(user2, user2.getUserNickname(), "KOR5", "ENG5", 12, false);
        FanRadio f6 = FanRadio.create(user2, user2.getUserNickname(), "KOR6", "ENG6", 34, false);
        FanRadio f7 = FanRadio.create(user2, user2.getUserNickname(), "KOR7", "ENG7", 65, false);

        em.persist(f1); em.persist(f2); em.persist(f3); em.persist(f4);
        em.persist(f5); em.persist(f6); em.persist(f7);

        //when
        List<DriverNumberProjection> driverNumberList = fanRadioRepository.getDriverNumberList();

        //then
        assertThat(driverNumberList).hasSize(1);
        assertThat(driverNumberList.get(0).getRadioTextKor()).isEqualTo(f7.getRadioTextKor());
    }

    @Test
    @DisplayName("라디오 단건 조회")
    void findDetailWithLikeYnTest() {
        //given
        FanRadio f1 = FanRadio.create(user1, user1.getUserNickname(), "KOR1", "ENG1", 22, false);
        Like like = Like.create(f1, user2.getUserSn());
        em.persist(f1); em.persist(like);
        em.flush(); em.clear();

        //when
        FanRadioDetailProjection detailWithLikeYn = fanRadioRepository.findDetailWithLikeYn(f1.getRadioSn(), user2.getUserSn());

        //then
        assertThat(detailWithLikeYn.getLikeYn()).isEqualTo(1);
        assertThat(detailWithLikeYn.getWriterNickname()).isEqualTo(f1.getRadioNickname());
    }

    @Test
    @DisplayName("내가 쓴 목록(myAllList): 닉네임 기준, 삭제 제외, radioSn DESC")
    void myAllListTest() {
        // given
        FanRadio a = FanRadio.create(user1, user1.getUserNickname(), "K1", "E1", 0, false);
        FanRadio b = FanRadio.create(user1, user1.getUserNickname(), "K2", "E2", 0, false);
        FanRadio c = FanRadio.create(user2, user2.getUserNickname(), "K3", "E3", 0, false);
        FanRadio deleted = FanRadio.create(user1, user1.getUserNickname(), "K4", "E4", 0, true);

        em.persist(a); em.persist(b); em.persist(c); em.persist(deleted);
        em.flush(); em.clear();

        // when
        List<FanRadio> mine = fanRadioRepository.myAllList(user1.getUserNickname());

        // then
        assertThat(mine).hasSize(2);
        assertThat(mine.get(0).getRadioSn()).isGreaterThan(mine.get(1).getRadioSn());
        assertThat(mine).allMatch(fr -> !fr.isRadioDeletedYn());
        assertThat(mine).allMatch(fr -> fr.getUser().getUserSn().equals(user1.getUserSn()));
    }

    @Test
    @DisplayName("좋아요 증가/감소(incrementLike/decrementLike) 및 조회(findLikeCount) - 0 아래로는 내려가지 않음")
    void likeCounterQueriesTest() {
        // given
        FanRadio fr = FanRadio.create(user1, "me", "K", "E", 1, false);
        em.persist(fr);
        em.flush(); em.clear();

        // when: +1
        int inc = fanRadioRepository.incrementLike(fr.getRadioSn());
        assertThat(inc).isEqualTo(1);
        em.flush(); em.clear();

        long afterInc = fanRadioRepository.findLikeCount(fr.getRadioSn());
        assertThat(afterInc).isEqualTo(2L);

        // when: -1
        int dec = fanRadioRepository.decrementLike(fr.getRadioSn());
        assertThat(dec).isEqualTo(1);
        em.flush(); em.clear();

        long afterDec = fanRadioRepository.findLikeCount(fr.getRadioSn());
        assertThat(afterDec).isEqualTo(1L);

        // when: 0에서 더 내리려 해도 0 유지
        fanRadioRepository.decrementLike(fr.getRadioSn());
        fanRadioRepository.decrementLike(fr.getRadioSn());
        em.flush(); em.clear();

        long nonNegative = fanRadioRepository.findLikeCount(fr.getRadioSn());
        assertThat(nonNegative).isEqualTo(0L);
    }

    @Test
    @DisplayName("살아있는 글 단건 조회(findAlive)")
    void findAliveTest() {
        // given
        FanRadio alive = FanRadio.create(user1, "alive", "K", "E", 0, false);
        FanRadio deleted = FanRadio.create(user1, "deleted", "K", "E", 0, true);
        em.persist(alive); em.persist(deleted);
        em.flush(); em.clear();

        // when
        Optional<FanRadio> ok = fanRadioRepository.findAlive(alive.getRadioSn());
        Optional<FanRadio> none = fanRadioRepository.findAlive(deleted.getRadioSn());

        // then
        assertThat(ok).isPresent();
        assertThat(ok.get().getRadioNickname()).isEqualTo("alive");
        assertThat(none).isEmpty();
    }
}
