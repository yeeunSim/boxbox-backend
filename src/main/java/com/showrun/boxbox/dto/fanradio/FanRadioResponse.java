package com.showrun.boxbox.dto.fanradio;

import com.showrun.boxbox.domain.FanRadio;
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

    public static FanRadioResponse from(FanRadio fanRadio) {
        return new FanRadioResponse(
                fanRadio.getRadioSn(),
                fanRadio.getRadioTextKor(),
                fanRadio.getRadioTextEng(),
                fanRadio.getRadioNickname()
        );
    }
}