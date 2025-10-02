package com.showrun.boxbox.service;

import com.showrun.boxbox.dto.user.LoginResponse;
import com.showrun.boxbox.dto.user.TokenResponse;
import com.showrun.boxbox.security.JwtToken;

public interface LoginService {
    LoginResponse login(String username, String password);

    TokenResponse refresh(String refreshToken);
}
