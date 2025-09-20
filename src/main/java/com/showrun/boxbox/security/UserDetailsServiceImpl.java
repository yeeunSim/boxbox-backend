package com.showrun.boxbox.security;

import com.showrun.boxbox.domain.Login;
import com.showrun.boxbox.dto.user.UserInfo;
import com.showrun.boxbox.repository.LoginRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LoginRepository loginRepository;

    public UserDetailsServiceImpl(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Login login = loginRepository.findByLoginEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(login.getLoginEmail())
                .password(login.getLoginPassword()) // BCrypt 해시 저장
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .accountLocked(false).accountExpired(false)
                .credentialsExpired(false).disabled(false)
                .build();
    }
}
