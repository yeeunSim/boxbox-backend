package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.*;
import com.showrun.boxbox.dto.fanradio.DriverNumberProjection;
import com.showrun.boxbox.repository.FanRadioRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class FanRadioRespositoryTest {

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
    void getRadioListByDriverNumber() {
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
}
