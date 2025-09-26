package com.showrun.boxbox.dto.fanradio;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DriverNumberListResponse {
    Long radioSn;
    Long radioNum;
    String radioNickname;
    String radioTextEng;
    String radioTextKor;
}
