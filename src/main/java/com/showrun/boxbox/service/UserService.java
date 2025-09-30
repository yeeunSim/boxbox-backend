package com.showrun.boxbox.service;

import com.showrun.boxbox.dto.user.UserInfo;
import com.showrun.boxbox.security.JwtUserDetails;

public interface UserService {
    String userRegister(UserInfo userInfo);

    boolean updateLanguagePre(Long userSn, boolean userLang);

    boolean ensureNicknameAvailable(String nickname);

    boolean ensureEmailAvailable(String email);
}