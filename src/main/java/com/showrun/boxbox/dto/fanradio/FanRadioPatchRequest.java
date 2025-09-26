package com.showrun.boxbox.dto.fanradio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FanRadioPatchRequest {
    private String lang; // 입력 언어 식별자("kor" | "eng")
    private String text; // 원문 텍스트
}
