package com.showrun.boxbox.service;

import com.showrun.boxbox.dto.fanradio.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FanRadioService {

    FanRadioResponse createRadio(String loginEmail, FanRadioRequest request);

    FanRadioResponse patchRadio(String loginEmail, Long radioSn, FanRadioPatchRequest request);

    FanRadioDeleteResponse deleteRadio(String loginEmail, Long radioSn);

    Slice<FanRadioRankResponse> getRadios(RadioSortType sort, Pageable pageable);

    Slice<FanRadioRankResponse> searchRadios(String nickname, RadioSortType sort, Pageable pageable);
}