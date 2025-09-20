package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.Login;
import com.showrun.boxbox.domain.Status;
import com.showrun.boxbox.domain.User;
import com.showrun.boxbox.dto.user.TokenResponse;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import com.showrun.boxbox.repository.LoginRepository;
import com.showrun.boxbox.repository.UserRepository;
import com.showrun.boxbox.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public TokenResponse login(String loginEmail, String loginPassword) {
        // 1) 인증 (이 과정에서 UserDetailsService가 호출됨)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginEmail, loginPassword)
        );
        UserDetails principal = (UserDetails) auth.getPrincipal();

        // 2) 액세스/리프레시 발급
        String access = jwtTokenProvider.createAccessToken(principal.getUsername(), principal.getAuthorities());
        String refresh = jwtTokenProvider.createRefreshToken(principal.getUsername());

        // 3) 리프레시 토큰 저장
        Login login = loginRepository.findByLoginEmail(principal.getUsername())
                .orElseThrow(() -> new BoxboxException(ErrorCode.LOGIN_FAILED));
        // refresh 만료 계산
        Date refreshExp = jwtTokenProvider.getExpiration(refresh);

        login.update(login.getLoginPassword(), refresh, login.getSalt()); // 비번 변경 없음(원형 유지)
        login.update(refresh, LocalDateTime.ofInstant(refreshExp.toInstant(), java.time.ZoneId.systemDefault()));

        // 4) 사용자 마지막 로그인/상태 업데이트
        User user = userRepository.findByLogin_LoginEmail(principal.getUsername())
                .orElseThrow(() -> new BoxboxException(ErrorCode.NOT_FOUND));
        user.update(Status.ACTIVE, LocalDateTime.now());

        return new TokenResponse(access, refresh);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        // 1) 토큰 유효성
        if (!jwtTokenProvider.validate(refreshToken))
            throw new BoxboxException(ErrorCode.INVALID_TOKEN);

        String email = jwtTokenProvider.getSubject(refreshToken);

        // 2) DB에 저장된 최신 리프레시와 일치하는지 (토큰 로테이션/탈취 방지)
        Login login = loginRepository.findByLoginEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "login not found"));

        if (!refreshToken.equals(login.getTokenValue()))
            throw new BoxboxException(ErrorCode.INVALID_TOKEN);

        if (login.getTokenExp() != null && login.getTokenExp().isBefore(LocalDateTime.now()))
            throw new BoxboxException(ErrorCode.EXPIRED_TOKEN);

        // 3) 새 토큰들 발급 + 로테이션 저장
        // 권한은 기본 ROLE_USER만 있으므로 null 전달로 생성해도 무방하지만 일관성 위해…
        String newAccess = jwtTokenProvider.createAccessToken(email, null);
        String newRefresh = jwtTokenProvider.createRefreshToken(email);

        Date refreshExp = io.jsonwebtoken.Jwts.parser().build()
                .parseSignedClaims(newRefresh).getPayload().getExpiration();
        login.update(newRefresh, LocalDateTime.ofInstant(refreshExp.toInstant(), java.time.ZoneId.systemDefault()));

        return new TokenResponse(newAccess, newRefresh);
    }
}
