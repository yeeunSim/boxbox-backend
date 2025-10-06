package com.showrun.boxbox.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeToggleResponse {
    private boolean liked;
    private long likes;
}
