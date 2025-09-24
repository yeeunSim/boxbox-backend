package com.showrun.boxbox.service;

import com.showrun.boxbox.dto.papago.PapagoTranslationResponse;

public interface PapagoService {
    PapagoTranslationResponse translation(String lang, String translationMsg);
}
