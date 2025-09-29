package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.FanRadio;
import com.showrun.boxbox.domain.Like;
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
    public Result toggleLike(Long radioSn, Long likeUserSn) {
        FanRadio fanRadio = fanRadioRepository.findAlive(radioSn)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 라디오입니다. radioSn=" + radioSn));

        boolean exists = likeRepository.exists(radioSn, likeUserSn);

        if (exists) {
            likeRepository.findOne(radioSn, likeUserSn).ifPresent(likeRepository::delete);
        } else {
            likeRepository.save(Like.create(fanRadio, likeUserSn));
        }

        long count = likeRepository.countByRadio(radioSn);
        fanRadioRepository.updateLikeCount(radioSn, (int) count);

        return new Result(!exists, count);
    }
}
