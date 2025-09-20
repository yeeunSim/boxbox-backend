package com.showrun.boxbox.controller;

import com.showrun.boxbox.dto.user.UserInfo;
import com.showrun.boxbox.repository.UserRepository;
import com.showrun.boxbox.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
