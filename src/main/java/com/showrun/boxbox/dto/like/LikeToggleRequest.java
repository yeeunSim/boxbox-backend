package com.showrun.boxbox.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeToggleRequest {
    private Long radioSn;
    private Long likeUserSn; // 인증 연동 시 @AuthenticationPrincipal에서 가져와도 됨
}
