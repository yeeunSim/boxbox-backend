package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.FanRadio;
import com.showrun.boxbox.domain.User;
import com.showrun.boxbox.dto.fanradio.FanRadioDeleteResponse;
import com.showrun.boxbox.dto.fanradio.FanRadioPatchRequest;
import com.showrun.boxbox.dto.fanradio.FanRadioRankResponse;
import com.showrun.boxbox.dto.fanradio.FanRadioRequest;
import com.showrun.boxbox.dto.fanradio.FanRadioResponse;
import com.showrun.boxbox.dto.fanradio.RadioSortType;
import com.showrun.boxbox.dto.papago.PapagoTranslationResponse;
import com.showrun.boxbox.repository.FanRadioRepository;
import com.showrun.boxbox.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FanRadioServiceImplTest {

    @Mock FanRadioRepository fanRadioRepository;
    @Mock UserRepository userRepository;
    @Mock PapagoService papagoService;

    FanRadioServiceImpl sut;

    @BeforeEach
    void setUp() {
        sut = new FanRadioServiceImpl(fanRadioRepository, userRepository, papagoService);
    }

    // createRadio
    @Nested
    @DisplayName("createRadio (성공)")
    class CreateRadio {

        @Test
        @DisplayName("작성자 존재 + 활성 글 2개 → 번역 후 저장 성공")
        void create_success() {
            String loginEmail = "me@example.com";

            User user = mock(User.class);
            when(user.getUserSn()).thenReturn(42L);
            when(user.getUserNickname()).thenReturn("yeeun");
            when(userRepository.findByLogin_LoginEmail(loginEmail)).thenReturn(Optional.of(user));

            when(fanRadioRepository.countByUser_UserSnAndRadioDeletedYnFalse(42L)).thenReturn(2L);

            PapagoTranslationResponse papago = new PapagoTranslationResponse(
                    "Hello", // translationEng
                    "안녕"   // translationKor
            );
            when(papagoService.translation("kor", "원문")).thenReturn(papago);

            // save가 반환하는 엔티티를 읽기만 할 수 있도록 mock으로 구성
            FanRadio saved = mock(FanRadio.class);
            when(saved.getRadioSn()).thenReturn(777L);
            when(saved.getRadioTextKor()).thenReturn("안녕");
            when(saved.getRadioTextEng()).thenReturn("Hello");
            when(saved.getUser()).thenReturn(user);
            when(fanRadioRepository.save(any(FanRadio.class))).thenReturn(saved);

            FanRadioRequest req = mock(FanRadioRequest.class);
            when(req.getLang()).thenReturn("kor");
            when(req.getText()).thenReturn("원문");

            FanRadioResponse res = sut.createRadio(loginEmail, req);

            assertThat(res.getRadioSn()).isEqualTo(777L);
            assertThat(res.getWriterNickname()).isEqualTo("yeeun");
            assertThat(res.getRadioTextKor()).isEqualTo("안녕");
            assertThat(res.getRadioTextEng()).isEqualTo("Hello");

            InOrder inOrder = inOrder(userRepository, fanRadioRepository, papagoService);
            inOrder.verify(userRepository).findByLogin_LoginEmail(loginEmail);
            inOrder.verify(fanRadioRepository).countByUser_UserSnAndRadioDeletedYnFalse(42L);
            inOrder.verify(papagoService).translation("kor", "원문");
            inOrder.verify(fanRadioRepository).save(any(FanRadio.class));
        }
    }

    // patchRadio
    @Nested
    @DisplayName("patchRadio (성공)")
    class PatchRadio {

        @Test
        @DisplayName("라디오 내용 수정 성공")
        void patch_success() {
            String loginEmail = "me@example.com";

            User user = mock(User.class);
            when(user.getUserSn()).thenReturn(42L);
            when(user.getUserNickname()).thenReturn("yeeun");
            when(userRepository.findByLogin_LoginEmail(loginEmail)).thenReturn(Optional.of(user));

            Long radioSn = 900L;

            // spy() = “진짜 객체 + 살짝 꾸미기”
            // 대부분은 진짜처럼 돌리고, 필요한 한두 곳만 내가 고친 값으로 보이게
            // 여기선 update는 그대로, getRadioSn만 우리가 정한 숫자로 보이게
            FanRadio real = FanRadio.create(user, "yeeun", "old-kor", "old-eng", 3, false);
            FanRadio existing = spy(real);
            when(existing.getRadioSn()).thenReturn(radioSn);

            when(fanRadioRepository.findByRadioSnAndUser_UserSnAndRadioDeletedYnFalse(radioSn, 42L))
                    .thenReturn(Optional.of(existing));

            PapagoTranslationResponse papago = new PapagoTranslationResponse(
                    "new-eng",
                    "new-kor"
            );
            when(papagoService.translation("kor", "패치원문")).thenReturn(papago);

            FanRadioPatchRequest req = mock(FanRadioPatchRequest.class);
            when(req.getLang()).thenReturn("kor");
            when(req.getText()).thenReturn("패치원문");

            FanRadioResponse res = sut.patchRadio(loginEmail, radioSn, req);

            assertThat(res.getRadioSn()).isEqualTo(radioSn);
            assertThat(res.getRadioTextKor()).isEqualTo("new-kor");
            assertThat(res.getRadioTextEng()).isEqualTo("new-eng");
            assertThat(res.getWriterNickname()).isEqualTo("yeeun");

            verify(fanRadioRepository).findByRadioSnAndUser_UserSnAndRadioDeletedYnFalse(radioSn, 42L);
            verify(papagoService).translation("kor", "패치원문");
            verifyNoMoreInteractions(fanRadioRepository);
        }
    }

    // deleteRadio
    @Nested
    @DisplayName("deleteRadio (성공)")
    class DeleteRadio {

        @Test
        @DisplayName("soft delete 성공")
        void delete_success() {
            String loginEmail = "me@example.com";

            User user = mock(User.class);
            when(user.getUserSn()).thenReturn(42L);
            when(userRepository.findByLogin_LoginEmail(loginEmail)).thenReturn(Optional.of(user));

            Long radioSn = 1000L;

            FanRadio existing = FanRadio.create(user, "yeeun", "kor", "eng", 0, false);
            when(fanRadioRepository.findByRadioSnAndUser_UserSn(radioSn, 42L))
                    .thenReturn(Optional.of(existing));

            when(fanRadioRepository.softDeleteByOwner(radioSn, 42L)).thenReturn(1);

            FanRadioDeleteResponse res = sut.deleteRadio(loginEmail, radioSn);

            assertThat(res.getRadioSn()).isEqualTo(radioSn);
            assertThat(res.isDeleted()).isTrue();

            InOrder inOrder = inOrder(userRepository, fanRadioRepository);
            inOrder.verify(userRepository).findByLogin_LoginEmail(loginEmail);
            inOrder.verify(fanRadioRepository).findByRadioSnAndUser_UserSn(radioSn, 42L);
            inOrder.verify(fanRadioRepository).softDeleteByOwner(radioSn, 42L);
        }
    }

    // getRadios
    @Nested
    @DisplayName("getRadios (성공)")
    class GetRadios {

        @Test
        @DisplayName("정렬 POPULAR → findPopular 호출, Slice 매핑 성공")
        void getRadios_success_popular() {
            Pageable pageable = PageRequest.of(0, 3);

            FanRadio a = radioMock(1L, "a", "K1", "E1", 10, LocalDateTime.of(2025,1,1,0,0));
            FanRadio b = radioMock(2L, "b", "K2", "E2",  9, LocalDateTime.of(2025,1,1,0,1));
            FanRadio c = radioMock(3L, "c", "K3", "E3",  8, LocalDateTime.of(2025,1,1,0,2));

            Slice<FanRadio> slice = new SliceImpl<>(List.of(a, b, c), pageable, true);
            when(fanRadioRepository.findPopular(pageable)).thenReturn(slice);

            Slice<FanRadioRankResponse> res = sut.getRadios(RadioSortType.POPULAR, pageable);

            assertThat(res.getContent()).hasSize(3);
            assertThat(res.hasNext()).isTrue();
            assertThat(res.getContent().get(0).getRadioSn()).isEqualTo(1L);

            verify(fanRadioRepository).findPopular(pageable);
            verifyNoInteractions(userRepository, papagoService);
        }

        @Test
        @DisplayName("정렬 LATEST → findLatest 호출, Slice 매핑 성공")
        void getRadios_success_latest() {
            Pageable pageable = PageRequest.of(1, 2); // page=1,size=2 → rank 시작 3

            FanRadio a = radioMock(10L, "x", "K4", "E4", 3, LocalDateTime.of(2025,1,2,0,0));
            FanRadio b = radioMock(11L, "y", "K5", "E5", 2, LocalDateTime.of(2025,1,2,0,1));

            Slice<FanRadio> slice = new SliceImpl<>(List.of(a, b), pageable, false);
            when(fanRadioRepository.findLatest(pageable)).thenReturn(slice);

            Slice<FanRadioRankResponse> res = sut.getRadios(RadioSortType.LATEST, pageable);

            assertThat(res.getContent()).hasSize(2);
            assertThat(res.hasNext()).isFalse();
            assertThat(res.getContent().get(0).getRank()).isEqualTo(3L);

            verify(fanRadioRepository).findLatest(pageable);
            verifyNoInteractions(userRepository, papagoService);
        }
    }

    // searchRadios
    @Nested
    @DisplayName("searchRadios (성공)")
    class SearchRadios {

        @Test
        @DisplayName("인기순 검색 → searchPopular 호출, Slice 매핑 성공")
        void search_success_popular() {
            String nickname = "ye";
            Pageable pageable = PageRequest.of(0, 2);

            FanRadio a = radioMock(21L, "yeeun", "K1", "E1", 7, LocalDateTime.of(2025,1,3,0,0));
            FanRadio b = radioMock(22L, "yes",   "K2", "E2", 6, LocalDateTime.of(2025,1,3,0,1));

            Slice<FanRadio> slice = new SliceImpl<>(List.of(a, b), pageable, true);
            when(fanRadioRepository.searchPopular(nickname, pageable)).thenReturn(slice);

            Slice<FanRadioRankResponse> res = sut.searchRadios(nickname, RadioSortType.POPULAR, pageable);

            assertThat(res.getContent()).hasSize(2);
            assertThat(res.hasNext()).isTrue();

            verify(fanRadioRepository).searchPopular(nickname, pageable);
            verifyNoInteractions(userRepository, papagoService);
        }

        @Test
        @DisplayName("최신순 검색 → searchLatest 호출, Slice 매핑 성공")
        void search_success_latest() {
            String nickname = "ye";
            Pageable pageable = PageRequest.of(2, 2); // 3페이지 → rank 시작 5

            FanRadio a = radioMock(31L, "yeye", "K3", "E3", 1, LocalDateTime.of(2025,1,4,0,0));
            FanRadio b = radioMock(32L, "yeon", "K4", "E4", 0, LocalDateTime.of(2025,1,4,0,1));

            Slice<FanRadio> slice = new SliceImpl<>(List.of(a, b), pageable, false);
            when(fanRadioRepository.searchLatest(nickname, pageable)).thenReturn(slice);

            Slice<FanRadioRankResponse> res = sut.searchRadios(nickname, RadioSortType.LATEST, pageable);

            assertThat(res.getContent()).hasSize(2);
            assertThat(res.hasNext()).isFalse();
            assertThat(res.getContent().get(0).getRank()).isEqualTo(5L);

            verify(fanRadioRepository).searchLatest(nickname, pageable);
            verifyNoInteractions(userRepository, papagoService);
        }
    }

    // 실제로 쓰는 게터만 스텁(불필요한 스텁은 하지 말기)
    private FanRadio radioMock(
            long sn, String nickname, String kor, String eng,
            int like, LocalDateTime createdAt
    ) {
        FanRadio fr = mock(FanRadio.class);
        when(fr.getRadioSn()).thenReturn(sn);
        when(fr.getRadioNickname()).thenReturn(nickname);
        when(fr.getRadioTextKor()).thenReturn(kor);
        when(fr.getRadioTextEng()).thenReturn(eng);
        when(fr.getRadioLikeCount()).thenReturn(like);
        when(fr.getRadioCreatedAt()).thenReturn(createdAt);
        return fr;
    }
}