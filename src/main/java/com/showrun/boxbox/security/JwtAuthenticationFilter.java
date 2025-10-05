package com.showrun.boxbox.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService uds) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = uds;
    }

    // com.showrun.boxbox.security.JwtAuthenticationFilter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTokenProvider.validate(token)) {
                Long userSn = jwtTokenProvider.getUserId(token);  // ★ subject=userSn
                String email = jwtTokenProvider.getEmail(token);  // 선택
                // roles를 토큰에서 꺼내 쓰고 싶다면 claim 파싱 메서드 하나 더 만들면 됨
                var user = new JwtUserDetails(userSn, email, List.of()); // 필요하면 권한 파싱
                var auth = new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }
}