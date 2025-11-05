package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.FanRadio;
import com.showrun.boxbox.domain.Like;
import com.showrun.boxbox.dto.like.LikeToggleResponse;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import com.showrun.boxbox.repository.FanRadioRepository;
import com.showrun.boxbox.repository.LikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @InjectMocks
    private LikeServiceImpl likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private FanRadioRepository fanRadioRepository;

    private final Long RADIO_SN = 1L;
    private final Long LIKE_USER_SN = 10L;


    private FanRadio createFanRadio() {
        return mock(FanRadio.class);
    }


    @Test
    @DisplayName("좋아요가 존재하지 않을 때, 좋아요를 생성하고 카운트를 증가시킨다.")
    void toggleLike_createNewLike() {

        // given
        FanRadio fanRadioBefore = createFanRadio();
        FanRadio fanRadioAfter = createFanRadio();
        int expectedLikeCount = 6; // 예상되는 최종 좋아요 수

        when(fanRadioRepository.findAlive(RADIO_SN)).thenReturn(Optional.of(fanRadioBefore));
        when(likeRepository.existsByFanRadio_radioSnAndLikeUserSn(RADIO_SN, LIKE_USER_SN)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(mock(Like.class)); // save 호출 스텁

        when(fanRadioRepository.findById(RADIO_SN)).thenReturn(Optional.of(fanRadioAfter));
        when(fanRadioAfter.getRadioLikeCount()).thenReturn(expectedLikeCount);

        // when
        LikeToggleResponse response = likeService.toggleLike(RADIO_SN, LIKE_USER_SN);

        // then
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(fanRadioRepository, times(1)).incrementLike(RADIO_SN);
        verify(likeRepository, never()).deleteByFanRadio_radioSnAndLikeUserSn(anyLong(), anyLong());

        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikes()).isEqualTo(expectedLikeCount);
    }

    @Test
    @DisplayName("좋아요가 이미 존재할 때, 좋아요를 삭제하고 카운트를 감소시킨다.")
    void toggleLike_deleteExistingLike() {

        // given
        FanRadio fanRadioBefore = createFanRadio();
        FanRadio fanRadioAfter = createFanRadio();
        int expectedLikeCount = 4;

        when(fanRadioRepository.findAlive(RADIO_SN)).thenReturn(Optional.of(fanRadioBefore));
        when(likeRepository.existsByFanRadio_radioSnAndLikeUserSn(RADIO_SN, LIKE_USER_SN)).thenReturn(true);

        when(likeRepository.deleteByFanRadio_radioSnAndLikeUserSn(RADIO_SN, LIKE_USER_SN))
                .thenReturn(1L);

        when(fanRadioRepository.findById(RADIO_SN)).thenReturn(Optional.of(fanRadioAfter));

        when(fanRadioAfter.getRadioLikeCount()).thenReturn(expectedLikeCount);

        // when
        LikeToggleResponse response = likeService.toggleLike(RADIO_SN, LIKE_USER_SN);

        // then
        verify(likeRepository, times(1)).deleteByFanRadio_radioSnAndLikeUserSn(RADIO_SN, LIKE_USER_SN);
        verify(fanRadioRepository, times(1)).decrementLike(RADIO_SN);
        verify(likeRepository, never()).save(any(Like.class));

        assertThat(response.isLiked()).isFalse();
        assertThat(response.getLikes()).isEqualTo(expectedLikeCount);
    }

    @Test
    @DisplayName("라디오가 존재하지 않거나 삭제되었을 경우 IllegalArgumentException이 발생한다.")
    void toggleLike_radioNotFound_throwsException() {

        // given
        when(fanRadioRepository.findAlive(RADIO_SN)).thenReturn(Optional.empty());

        // when, then
        assertThrows(IllegalArgumentException.class, () -> {
            likeService.toggleLike(RADIO_SN, LIKE_USER_SN);
        });

        verify(likeRepository, never()).existsByFanRadio_radioSnAndLikeUserSn(anyLong(), anyLong());
        verify(fanRadioRepository, never()).incrementLike(anyLong());
        verify(fanRadioRepository, never()).decrementLike(anyLong());
    }

    @Test
    @DisplayName("좋아요 토글 후 FanRadio를 다시 조회하지 못하면 BoxboxException(RADIO_NOT_FOUND)이 발생한다.")
    void toggleLike_newRadioNotFound_throwsBoxboxException() {

        // given
        FanRadio fanRadioBefore = createFanRadio();

        when(fanRadioRepository.findAlive(RADIO_SN)).thenReturn(Optional.of(fanRadioBefore));
        when(likeRepository.existsByFanRadio_radioSnAndLikeUserSn(RADIO_SN, LIKE_USER_SN)).thenReturn(false);

        when(likeRepository.save(any(Like.class))).thenReturn(mock(Like.class));
        verify(fanRadioRepository, times(0)).incrementLike(anyLong());

        when(fanRadioRepository.findById(RADIO_SN)).thenReturn(Optional.empty());

        // when
        BoxboxException exception = assertThrows(BoxboxException.class, () -> {
            likeService.toggleLike(RADIO_SN, LIKE_USER_SN);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RADIO_NOT_FOUND);
        verify(fanRadioRepository, times(1)).incrementLike(RADIO_SN);
    }
}