package com.showrun.boxbox.service;

import com.showrun.boxbox.dto.like.LikeToggleResponse;

public interface LikeService {

    LikeToggleResponse toggleLike(Long radioSn, Long likeUserSn);
}
