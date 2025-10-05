package com.showrun.boxbox.controller;

import com.showrun.boxbox.dto.common.ApiResponse;
import com.showrun.boxbox.dto.like.LikeToggleRequest;
import com.showrun.boxbox.dto.like.LikeToggleResponse;
import com.showrun.boxbox.security.JwtUserDetails;
import com.showrun.boxbox.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/radio-like")
    public ResponseEntity<ApiResponse<LikeToggleResponse>> toggle(
            @RequestBody LikeToggleRequest request,
            @AuthenticationPrincipal JwtUserDetails user
    ) {
        LikeService.Result result = likeService.toggleLike(request.getRadioSn(), user.getUserSn());

        return ResponseEntity.ok(
                ApiResponse.ok("처리 성공", new LikeToggleResponse(result.liked(), result.likeCount()))
        );
    }
}
