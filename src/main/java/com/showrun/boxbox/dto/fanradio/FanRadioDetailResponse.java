package com.showrun.boxbox.dto.fanradio;

import com.showrun.boxbox.domain.FanRadio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FanRadioDetailResponse {
    private Long radioSn;
    private String radioTextKor;
    private String radioTextEng;
    private String writerNickname;
    private Boolean likeYn;

    @Builder
    public static FanRadioDetailResponse from(FanRadioDetailProjection fanRadio) {
        return new FanRadioDetailResponse(
                fanRadio.getRadioSn(),
                fanRadio.getRadioTextKor(),
                fanRadio.getRadioTextEng(),
                fanRadio.getWriterNickname(),
                fanRadio.getLikeYn() != null && fanRadio.getLikeYn() != 0
        );
    }
}
