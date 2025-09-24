package com.showrun.boxbox.security;

import com.showrun.boxbox.domain.Login;
import com.showrun.boxbox.domain.User;
import com.showrun.boxbox.repository.LoginRepository;
import com.showrun.boxbox.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LoginRepository loginRepository;
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(LoginRepository loginRepository, UserRepository userRepository) {
        this.loginRepository = loginRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByLogin_LoginEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new JwtUserDetails(
                user.getUserSn(),
                user.getLogin().getLoginEmail(),
                user.getUserNickname(),
                user.getLogin().getLoginPassword(),                 // ★ 인코딩된 비번
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}