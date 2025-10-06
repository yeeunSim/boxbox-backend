package com.showrun.boxbox.controller;

import com.showrun.boxbox.dto.login.LoginRequest;
import com.showrun.boxbox.dto.user.LoginResponse;
import com.showrun.boxbox.dto.user.TokenResponse;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import com.showrun.boxbox.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest.getLoginEmail(), loginRequest.getLoginPw());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BoxboxException(ErrorCode.INVALID_TOKEN);
        }
        return loginService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie delete = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/refresh")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, delete.toString())
                .build();
    }
}
