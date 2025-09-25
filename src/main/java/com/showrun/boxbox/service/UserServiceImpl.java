package com.showrun.boxbox.service;


import com.showrun.boxbox.domain.Gender;
import com.showrun.boxbox.domain.Login;
import com.showrun.boxbox.domain.Status;
import com.showrun.boxbox.domain.User;
import com.showrun.boxbox.dto.user.UserInfo;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
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

    @Override
    @Transactional
    public boolean updateLanguagePre(Long userSn, boolean userLangPre) {
        try {
            User user = userRepository.findById(userSn)
                    .orElseThrow(() -> new BoxboxException(ErrorCode.USER_NOT_FOUND));
            user.updateLang(userLangPre);
            return user.isUserLang();
        } catch (BoxboxException e) {
            // 이미 의미있는 코드로 포장된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            // DB, 트랜잭션 등 기타 예외 → 언어 설정 전용 코드로 래핑
            throw new BoxboxException(ErrorCode.LANG_CHANGE_FAILED, e);
        }
}   }
