package com.showrun.boxbox.dto.lang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LanguagePreferenceRequest {
    // false = 영어, true = 한국어
    private boolean userLang;
}