package com.showrun.boxbox.dto.papago;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PapagoTranslationResult {

    private Message message;

    @Data
    public static class Message {
        private Result result;
    }

    @Data
    public static class Result {

        private String srcLangType;

        private String tarLangType;

        private String translatedText;

    }
}
