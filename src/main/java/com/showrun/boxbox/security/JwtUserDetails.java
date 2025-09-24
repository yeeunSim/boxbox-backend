// com.showrun.boxbox.security.JwtUserDetails
package com.showrun.boxbox.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class JwtUserDetails implements UserDetails {
    private final Long userSn;
    private final String email;              // 로그인 아이디(이메일)
    private final String nickname;           // 선택
    private final String password;           // ★ 로그인 시 검증용(암호화된 값)
    private final Collection<? extends GrantedAuthority> authorities;

    // 로그인(인증)에서 사용할 풀 생성자
    public JwtUserDetails(Long userSn, String email, String nickname, String password,
                          Collection<? extends GrantedAuthority> authorities) {
        this.userSn = userSn;
        this.email = email;
        this.nickname = nickname;
        this.password = password; // ★ DaoAuthenticationProvider가 비교
        this.authorities = authorities == null ? List.of() : authorities;
    }

    // 필터에서 토큰만으로 주입할 때(비번 불필요) 편의 생성자
    public JwtUserDetails(Long userSn, String email, Collection<? extends GrantedAuthority> authorities) {
        this(userSn, email, null, null, authorities);
    }

    @Override public String getUsername() { return email == null ? String.valueOf(userSn) : email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
