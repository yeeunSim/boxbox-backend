package com.showrun.boxbox.dto.user;

import com.showrun.boxbox.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    String loginEmail;
    String loginPw;
    String userNickname;
    LocalDate userBirth;
    Gender userGender;
    String svcUsePcyAgmtYn;
    String psInfoProcAgmtYn;
}
