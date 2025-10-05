package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.FanRadio;
import com.showrun.boxbox.dto.fanradio.DriverNumberListResponse;
import com.showrun.boxbox.dto.fanradio.FanRadioDeleteResponse;
import com.showrun.boxbox.dto.fanradio.FanRadioRequest;
import com.showrun.boxbox.dto.fanradio.FanRadioResponse;
import com.showrun.boxbox.dto.fanradio.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FanRadioService {

    FanRadioResponse createRadio(String loginEmail, FanRadioRequest request);

    List<DriverNumberListResponse> getDriverNumberList();

    FanRadioDetailResponse getRadioByRadioSn(Long radioSn, Long userSn);

    FanRadioResponse patchRadio(String loginEmail, Long radioSn, FanRadioPatchRequest request);

    FanRadioDeleteResponse deleteRadio(String loginEmail, Long radioSn);

    Slice<FanRadioRankResponse> getRadios(RadioSortType sort, Pageable pageable);

    Slice<FanRadioRankResponse> searchRadios(String nickname, RadioSortType sort, Pageable pageable);

    List<FanRadioResponse> getMyRadios(Long loginEmail);
}