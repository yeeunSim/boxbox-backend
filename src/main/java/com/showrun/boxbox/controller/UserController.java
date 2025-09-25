package com.showrun.boxbox.controller;

import com.showrun.boxbox.dto.lang.LanguagePreferenceRequest;
import com.showrun.boxbox.dto.user.UserInfo;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import com.showrun.boxbox.repository.UserRepository;
import com.showrun.boxbox.security.JwtUserDetails;
import com.showrun.boxbox.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @RequestBody LanguagePreferenceRequest request) {
        try {
            boolean result = userService.updateLanguagePre(userDetails.getUserSn(), request.isUserLang());
            return ResponseEntity.ok(ApiResponse.ok("언어 설정이 변경되었습니다.", result)); // ApiResponse 포맷
        } catch (BoxboxException e) {
            // 서비스에서 이미 의미있는 ErrorCode로 래핑되어 올라오면 그대로 GlobalAdvice가 처리
            throw e;
        } catch (Exception e) {
            // 알 수 없는 예외 → 언어설정 전용 에러코드로 래핑
            throw new BoxboxException(ErrorCode.LANG_CHANGE_FAILED, e);
        }
    }
}