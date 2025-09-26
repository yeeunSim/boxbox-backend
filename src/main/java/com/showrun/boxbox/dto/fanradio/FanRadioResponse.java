package com.showrun.boxbox.dto.fanradio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FanRadioResponse {
    private Long radioSn;
    private String radioTextKor;
    private String radioTextEng;
    private String writerNickname;
}