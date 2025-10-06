package com.showrun.boxbox.service;

import com.showrun.boxbox.dto.user.LoginResponse;
import com.showrun.boxbox.dto.user.TokenResponse;
import com.showrun.boxbox.security.JwtToken;
import org.springframework.http.ResponseEntity;

public interface LoginService {
    ResponseEntity<LoginResponse> login(String username, String password);

    ResponseEntity<TokenResponse> refresh(String refreshToken);
}
