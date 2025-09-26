package com.showrun.boxbox.dto.fanradio;

import com.showrun.boxbox.domain.FanRadio;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FanRadioRankResponse {
    private long rank; // 페이지 기준 1-base 순위
    private Long radioSn;
    private String writerNickname;
    private String previewKor;
    private String previewEng;
    private int likeCount;
    private LocalDateTime createdAt;

    public static FanRadioRankResponse fromWithRank(FanRadio f, long rank) {
        return new FanRadioRankResponse(
                rank,
                f.getRadioSn(),
                f.getRadioNickname(),
                cut(f.getRadioTextKor()),
                cut(f.getRadioTextEng()),
                f.getRadioLikeCount(),
                f.getRadioCreatedAt()
        );
    }

    private static String cut(String s) {
        if (s == null) return null;
        return s.length() > 50 ? s.substring(0, 50) + "..." : s;
    }
}