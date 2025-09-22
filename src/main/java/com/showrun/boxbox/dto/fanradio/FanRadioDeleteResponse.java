package com.showrun.boxbox.dto.fanradio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FanRadioDeleteResponse {
    private Long radioId;
    private boolean deleted;
}
