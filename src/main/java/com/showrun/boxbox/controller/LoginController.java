package com.showrun.boxbox.controller;

import com.showrun.boxbox.dto.login.LoginRequest;
import com.showrun.boxbox.dto.user.TokenResponse;
import com.showrun.boxbox.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.login(loginRequest.getLoginEmail(), loginRequest.getLoginPw()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody String refreshToken) {
        return ResponseEntity.ok(loginService.refresh(refreshToken));
    }
}
