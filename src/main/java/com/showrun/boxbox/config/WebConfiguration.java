package com.showrun.boxbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

public class WebConfiguration {
    // 1) 공용 CORS 설정
    @Configuration
    public class CorsConfig {

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration cfg = new CorsConfiguration();
            // 개발/운영에 맞게 정확한 Origin으로 바꾸세요
            cfg.setAllowedOriginPatterns(List.of(
                    "http://localhost:5173",
                    "http://localhost:3000",
                    "https://web.example.com"
            ));
            cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
            cfg.setAllowedHeaders(List.of("*"));
            cfg.setExposedHeaders(List.of("Authorization", "RefreshToken"));
            cfg.setAllowCredentials(true);     // 쿠키/세션 또는 Authorization 포함 요청
            cfg.setMaxAge(3600L);              // 프리플라이트 캐시

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", cfg);
            return source;
        }
    }
}
