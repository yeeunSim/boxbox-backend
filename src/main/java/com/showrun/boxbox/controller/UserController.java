package com.showrun.boxbox.controller;

import com.showrun.boxbox.dto.lang.LanguagePreferenceRequest;
import com.showrun.boxbox.dto.user.UserInfo;
import com.showrun.boxbox.security.JwtUserDetails;
import com.showrun.boxbox.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.showrun.boxbox.dto.common.ApiResponse;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> userRegister(@RequestBody UserInfo userInfo) {
        userService.userRegister(userInfo);
        return ResponseEntity.ok().body("회원가입 성공");
    }

    @PatchMapping("/user/lang/preferences")
    public ResponseEntity<ApiResponse<Boolean>> changeLang(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @RequestBody LanguagePreferenceRequest request
    ) {
        boolean result = userService.updateLanguagePre(userDetails.getUserSn(), request.isUserLang());
        return ResponseEntity.ok(ApiResponse.ok("언어 설정이 변경되었습니다.", result));
    }

    @GetMapping("/sign-up/nickname-check")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam("nickname") String nickname) {

        return ResponseEntity.ok(
                ApiResponse.ok("사용 가능한 닉네임입니다.", userService.ensureNicknameAvailable(nickname)));
    }

    @GetMapping("/sign-up/email-check")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam("email") String email) {

        return ResponseEntity.ok(
                ApiResponse.ok("사용 가능한 이메일입니다.", userService.ensureEmailAvailable(email))
        );
    }
}