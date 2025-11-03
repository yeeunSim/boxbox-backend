package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.Gender;
import com.showrun.boxbox.domain.Login;
import com.showrun.boxbox.domain.User;
import com.showrun.boxbox.dto.user.LoginResponse;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import com.showrun.boxbox.repository.LoginRepository;
import com.showrun.boxbox.repository.UserRepository;
import com.showrun.boxbox.security.JwtTokenProvider;
import com.showrun.boxbox.security.JwtUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock UserRepository userRepository;
    @Mock LoginRepository loginRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks LoginServiceImpl loginService;

    @Test
    @DisplayName("로그인 테스트")
    void login_성공() {
        //given
        String email = "loginEmail1";
        String rawPw = "pw1";
        Long userSn = 100L;

        // (1) AuthenticationManager.authenticate(...) 스텁
        JwtUserDetails principal = mock(JwtUserDetails.class);
        when(principal.getUserSn()).thenReturn(userSn);
        when(principal.getEmail()).thenReturn(email);
        when(principal.getAuthorities()).thenReturn(Collections.emptyList());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        // (2) 토큰 발급/만료 스텁
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        when(jwtTokenProvider.createAccessToken(eq(userSn), eq(email), any()))
                .thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(eq(String.valueOf(userSn))))
                .thenReturn(refreshToken);
        Date exp = new Date(System.currentTimeMillis() + Duration.ofDays(14).toMillis());
        when(jwtTokenProvider.getExpiration(eq(refreshToken)))
                .thenReturn(exp);

        // (3) 리포지토리 스텁
        Login login = mock(Login.class);
        when(login.getLoginPassword()).thenReturn("hashed-pw");
        when(login.getSalt()).thenReturn("salt");
        when(login.getLoginEmail()).thenReturn(email);
        when(loginRepository.findByLoginEmail(eq(email)))
                .thenReturn(Optional.of(login));

        User user = mock(User.class);
        when(user.getUserNickname()).thenReturn("nickname1");
        when(user.getUserGender()).thenReturn(Gender.F);
        when(userRepository.findById(eq(userSn)))
                .thenReturn(Optional.of(user));

        //then
        ResponseEntity<LoginResponse> loginResponseEntity = loginService.login(email, rawPw);

        //then
        String setCookie = loginResponseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).contains("refreshToken=" + refreshToken);

        LoginResponse body = loginResponseEntity.getBody();
        assertThat(body.getUser().getLoginEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void login_BadCredentialsException_실패() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        // when & then
        assertThatThrownBy(() -> loginService.login("e@e.com", "x"))
                .isInstanceOf(BoxboxException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LOGIN_FAILED);
    }

    @Test
    void login_AuthenticationException_실패() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("auth problem") {});

        // when & then
        assertThatThrownBy(() -> loginService.login("e@e.com", "x"))
                .isInstanceOf(BoxboxException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHORIZED);
    }
}