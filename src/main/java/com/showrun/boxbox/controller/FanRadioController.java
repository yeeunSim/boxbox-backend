package com.showrun.boxbox.controller;

import com.showrun.boxbox.dto.common.ApiResponse;
import com.showrun.boxbox.dto.fanradio.FanRadioDeleteResponse;
import com.showrun.boxbox.dto.fanradio.FanRadioRequest;
import com.showrun.boxbox.dto.fanradio.FanRadioResponse;
import com.showrun.boxbox.service.FanRadioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/radio")
public class FanRadioController {

    private final FanRadioService fanRadioService;

    @PostMapping("/create-radio")
    public ResponseEntity<FanRadioResponse> createRadio(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FanRadioRequest request
    ) {
        String loginEmail = userDetails.getUsername();
        FanRadioResponse response = fanRadioService.createRadio(loginEmail, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-radio/{radioId}")
    public ResponseEntity<ApiResponse<FanRadioDeleteResponse>> deleteRadio(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long radioId
    ) {
        String loginEmail = userDetails.getUsername();
        FanRadioDeleteResponse result = fanRadioService.deleteMyRadio(loginEmail, radioId);

        return ResponseEntity.ok(
                ApiResponse.ok("라디오가 삭제되었습니다.", result)
        );
    }
}
