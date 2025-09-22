package com.showrun.boxbox.service;

import com.showrun.boxbox.dto.fanradio.FanRadioDeleteResponse;
import com.showrun.boxbox.dto.fanradio.FanRadioRequest;
import com.showrun.boxbox.dto.fanradio.FanRadioResponse;

public interface FanRadioService {
    FanRadioResponse createRadio(String loginEmail, FanRadioRequest request);
    FanRadioDeleteResponse deleteMyRadio(String loginEmail, Long radioId);
}
