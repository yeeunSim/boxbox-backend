package com.showrun.boxbox.controller;

import com.showrun.boxbox.dto.common.ApiResponse;
import com.showrun.boxbox.dto.fanradio.*;
import com.showrun.boxbox.security.JwtUserDetails;
import com.showrun.boxbox.service.FanRadioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/radio")
public class FanRadioController {

    private final FanRadioService fanRadioService;

    @PostMapping("/create-radio")
    public ResponseEntity<ApiResponse<FanRadioResponse>> createRadio(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @RequestBody FanRadioRequest request
    ) {
        FanRadioResponse result = fanRadioService.createRadio(userDetails.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.ok("라디오가 생성되었습니다.", result));
    }

    @PatchMapping("/patch-radio/{radioSn}")
    public ResponseEntity<ApiResponse<FanRadioResponse>> patchRadio(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @PathVariable Long radioSn,
            @RequestBody FanRadioPatchRequest request
    ) {
        FanRadioResponse updated = fanRadioService.patchRadio(userDetails.getEmail(), radioSn, request);
        return ResponseEntity.ok(ApiResponse.ok("라디오가 수정되었습니다.", updated));
    }

    @DeleteMapping("/delete-radio/{radioSn}")
    public ResponseEntity<ApiResponse<FanRadioDeleteResponse>> deleteRadio(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @PathVariable Long radioSn
    ) {
        FanRadioDeleteResponse result = fanRadioService.deleteRadio(userDetails.getEmail(), radioSn);
        return ResponseEntity.ok(ApiResponse.ok("라디오가 삭제되었습니다.", result));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Slice<FanRadioRankResponse>>> list(
            @RequestParam(defaultValue = "POPULAR") RadioSortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<FanRadioRankResponse> result = fanRadioService.getRadios(sort, pageable);
        return ResponseEntity.ok(ApiResponse.ok("전체 랭킹이 조회되었습니다.", result));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Slice<FanRadioRankResponse>>> search(
            @RequestParam String nickname,
            @RequestParam(defaultValue = "POPULAR") RadioSortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<FanRadioRankResponse> result = fanRadioService.searchRadios(nickname, sort, pageable);
        return ResponseEntity.ok(ApiResponse.ok("닉네임으로 조회되었습니다.", result));
    }
}
