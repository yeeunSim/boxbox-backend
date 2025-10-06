package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.FanRadio;
import com.showrun.boxbox.domain.Like;
import com.showrun.boxbox.dto.like.LikeToggleResponse;
import com.showrun.boxbox.repository.FanRadioRepository;
import com.showrun.boxbox.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final FanRadioRepository fanRadioRepository;

    @Override
    @Transactional
    public LikeToggleResponse toggleLike(Long radioSn, Long likeUserSn) {
        FanRadio fanRadio = fanRadioRepository.findAlive(radioSn)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 라디오입니다. radioSn=" + radioSn));

        boolean exists = likeRepository.existsByFanRadio_radioSnAndLikeUserSn(radioSn, likeUserSn);

        boolean nowLiked;

        if (exists) {
            // 삭제 & -1
            likeRepository.deleteByFanRadio_radioSnAndLikeUserSn(radioSn, likeUserSn);
            fanRadioRepository.decrementLike(radioSn);
            nowLiked = false;
        } else {
            // 생성 & +1
            likeRepository.save(Like.create(fanRadio, likeUserSn));
            fanRadioRepository.incrementLike(radioSn);
            nowLiked = true;
        }

        long likeCount = fanRadioRepository.findLikeCount(radioSn);

        return new LikeToggleResponse(nowLiked);
    }

}
