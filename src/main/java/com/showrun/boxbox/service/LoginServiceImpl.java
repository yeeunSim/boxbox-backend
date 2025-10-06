package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.Login;
import com.showrun.boxbox.domain.Status;
import com.showrun.boxbox.domain.User;
import com.showrun.boxbox.dto.user.LoginResponse;
import com.showrun.boxbox.dto.user.TokenResponse;
import com.showrun.boxbox.dto.user.UserInfo;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import com.showrun.boxbox.repository.LoginRepository;
import com.showrun.boxbox.repository.UserRepository;
import com.showrun.boxbox.security.JwtTokenProvider;
import com.showrun.boxbox.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<LoginResponse> login(String loginEmail, String loginPassword) {
        // 1) 인증
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginEmail, loginPassword)
        );
        JwtUserDetails principal = (JwtUserDetails) auth.getPrincipal();

        Long userSn = principal.getUserSn();
        String email = principal.getEmail();

        // 2) 액세스/리프레시 발급 (★ userSn 기반)
        String access = jwtTokenProvider.createAccessToken(userSn, email, principal.getAuthorities());
        String refresh = jwtTokenProvider.createRefreshToken(String.valueOf(userSn));

        // 3) 리프레시 토큰 저장
        Login login = loginRepository.findByLoginEmail(email)
                .orElseThrow(() -> new BoxboxException(ErrorCode.LOGIN_FAILED));

        Date refreshExp = jwtTokenProvider.getExpiration(refresh);
        // 비밀번호는 그대로 유지
        login.update(login.getLoginPassword(), refresh, login.getSalt());
        login.update(refresh, LocalDateTime.ofInstant(refreshExp.toInstant(), java.time.ZoneId.systemDefault()));

        // 4) 마지막 로그인/상태 업데이트
        User user = userRepository.findById(userSn)
                .orElseThrow(() -> new BoxboxException(ErrorCode.NOT_FOUND));
        user.update(Status.ACTIVE, LocalDateTime.now());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refresh)
                .httpOnly(true)
                .secure(true) // 운영: true, 로컬 dev: false
                .sameSite("Lax") // 크로스 도메인일 때. same-site면 "Lax"
                .path("/refresh")
                .maxAge(Duration.ofDays(14))
                .build();

        UserInfo userInfo = UserInfo.builder()
                .loginEmail(login.getLoginEmail())
                .userNickname(user.getUserNickname())
                .userGender(user.getUserGender()).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(access, userInfo));
    }

    @Transactional
    public ResponseEntity<TokenResponse> refresh(String refreshToken) {
        // 1) 토큰 유효성
        if (!jwtTokenProvider.validate(refreshToken))
            throw new BoxboxException(ErrorCode.INVALID_TOKEN);

        // ★ subject=userSn
        Long userSn = jwtTokenProvider.getUserId(refreshToken);

        // 2) DB에 저장된 최신 리프레시와 일치 확인
        Login login = loginRepository.findByUser_UserSn(userSn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "login not found"));

        if (!refreshToken.equals(login.getTokenValue()))
            throw new BoxboxException(ErrorCode.INVALID_TOKEN);

        if (login.getTokenExp() != null && login.getTokenExp().isBefore(LocalDateTime.now()))
            throw new BoxboxException(ErrorCode.EXPIRED_TOKEN);

        // 3) 새 토큰 발급 (★ userSn 기반) + 로테이션 저장
        String email = login.getLoginEmail(); // 액세스 토큰 클레임에 넣을 부가 정보
        String newAccess = jwtTokenProvider.createAccessToken(userSn, email, null);
        String newRefresh = jwtTokenProvider.createRefreshToken(String.valueOf(userSn));

        Date refreshExp = jwtTokenProvider.getExpiration(newRefresh);
        login.update(newRefresh, LocalDateTime.ofInstant(refreshExp.toInstant(), java.time.ZoneId.systemDefault()));

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefresh)
                .httpOnly(true)
                .secure(true) // 운영: true, 로컬 dev: false
                .sameSite("Lax") // 크로스 도메인일 때. same-site면 "Lax"
                .path("/refresh")
                .maxAge(Duration.ofDays(14))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new TokenResponse(newAccess));
    }

}