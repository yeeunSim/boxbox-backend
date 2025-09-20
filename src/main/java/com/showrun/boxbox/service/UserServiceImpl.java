package com.showrun.boxbox.service;


import com.showrun.boxbox.domain.Gender;
import com.showrun.boxbox.domain.Login;
import com.showrun.boxbox.domain.Status;
import com.showrun.boxbox.domain.User;
import com.showrun.boxbox.dto.user.UserInfo;
import com.showrun.boxbox.repository.LoginRepository;
import com.showrun.boxbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.showrun.boxbox.domain.Status.ACTIVE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String userRegister(UserInfo userInfo) {
        Login login = Login.builder()
                .loginEmail(userInfo.getLoginEmail())
                .loginPassword(passwordEncoder.encode(userInfo.getLoginPw()))
                .build();

        User user = User.builder()
                .login(login)
                .userNickname(userInfo.getUserNickname())
                .userBirth(userInfo.getUserBirth())
                .userGender(userInfo.getUserGender())
                .userStatus(ACTIVE)
                .userDeletedYn(false)
                .psInfoProcAgmtYn(true)
                .svcUsePcyAgmtYn(true)
                .build();



        User userSave = userRepository.save(user);
        Login loginSave = loginRepository.save(login);

        return "회원가입을 성공했습니다.";
    }
}
