package com.showrun.boxbox.service;

import com.showrun.boxbox.dto.papago.PapagoTranslationResponse;
import com.showrun.boxbox.dto.papago.PapagoTranslationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PapagoServiceImpl implements PapagoService {
    @Value("${ncp.papago.client-id}")
    private String papagoClientId;

    @Value("${ncp.papago.client-secret}")
    private String papagoClientSecret;

    public PapagoTranslationResponse translation(String lang, String translationMsg) {
        WebClient webClient = WebClient.create("https://papago.apigw.ntruss.com");

        String sourceLang = lang.equals("kor") ? "ko" : "en";
        String targetLang = lang.equals("kor") ? "en" : "ko";

        PapagoTranslationResult response = webClient.post()
                .uri("/nmt/v1/translation")
                .headers(httpHeaders -> { // *^-^*V
                    httpHeaders.add("x-ncp-apigw-api-key-id", papagoClientId);
                    httpHeaders.add("x-ncp-apigw-api-key", papagoClientSecret);
                })
                .bodyValue(Map.of("source", sourceLang, "target", targetLang, "text",  translationMsg))
                .retrieve()
                .bodyToMono(PapagoTranslationResult.class)
                .block();

        if(lang.equals("kor")) return new PapagoTranslationResponse(response.getMessage().getResult().getTranslatedText(), translationMsg);
        //(ㅇ.ㅇ) // ㄴ(ㅍ.ㅁ)ㄱ   (0_<) // (>a<) (^V^) (^A^)
/*
\    /\
 )  ( ')
(  /  )
 \(__)|
*/

/*
|\_/|
|q p|   /}
( 0 )"""\
|"^"`    |
||_/=\\__|
*/
        return new PapagoTranslationResponse(translationMsg, response.getMessage().getResult().getTranslatedText());
    }
}
