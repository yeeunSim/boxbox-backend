package com.showrun.boxbox.service;

public interface LikeService {

    Result toggleLike(Long radioSn, Long likeUserSn);

    record Result(boolean liked, long likeCount) {}
}
