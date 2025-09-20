package com.showrun.boxbox.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import org.springframework.security.core.GrantedAuthority;

@Component
public class JwtTokenProvider {

    private SecretKey key;

    @Value("${jwt.secret}")
    private String secret; // 최소 32바이트 권장(HS256)

    @Value("${jwt.access-token-validity-seconds}")
    private long accessValiditySec;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshValiditySec;

    @PostConstruct
    void init() {
        // Base64로 저장했다면 Decoders.BASE64.decode(secret) 사용
        // 여기선 UTF-8 바로 사용(개발용). 운영은 Base64 권장.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String subject, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessValiditySec * 1000);
        return Jwts.builder()
                .subject(subject)
                .claim("roles", authorities == null ? null :
                        authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key, Jwts.SIG.HS256)   // ★ 서명
                .compact();
    }

    public String createRefreshToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshValiditySec * 1000);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key, Jwts.SIG.HS256)   // ★ 서명
                .compact();
    }

    public boolean validate(String jwt) {
        try {
            // ★ 검증키 연결해서 파싱
            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubject(String jwt) {
        return parse(jwt).getSubject();
    }

    public Date getExpiration(String jwt) {
        return parse(jwt).getExpiration();
    }

    private Claims parse(String jwt) {
        return Jwts.parser().verifyWith(key).build()   // ★ verifyWith 필수
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
