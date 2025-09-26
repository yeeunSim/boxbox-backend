package com.showrun.boxbox.service;

import com.showrun.boxbox.domain.FanRadio;
import com.showrun.boxbox.domain.User;
import com.showrun.boxbox.dto.fanradio.*;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import com.showrun.boxbox.repository.FanRadioRepository;
import com.showrun.boxbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FanRadioServiceImpl implements FanRadioService {
    private final FanRadioRepository fanRadioRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FanRadioResponse createRadio(String loginEmail, FanRadioRequest request) {
        User user = userRepository.findByLogin_LoginEmail(loginEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        FanRadio fanRadio = FanRadio.builder()
                .user(user)
                .radioNickname(user.getUserNickname())
                .radioTextKor(request.getRadioTextKor())
                .radioTextEng(request.getRadioTextEng())
                .radioLikeCount(0)
                .radioDeletedYn(false)
                .build();

        FanRadio saved = fanRadioRepository.save(fanRadio);

        return new FanRadioResponse(
                saved.getRadioSn(),
                saved.getRadioTextKor(),
                saved.getRadioTextEng(),
                saved.getUser().getUserNickname()
        );
    }

    @Override
    @Transactional
    public FanRadioDeleteResponse deleteMyRadio(String loginEmail, Long radioId) {
        User user = userRepository.findByLogin_LoginEmail(loginEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int updated = fanRadioRepository.softDeleteByOwner(radioId, user.getUserSn());
        if (updated == 0) {
            // 소유자가 아니거나(403), 이미 삭제/존재X(404) 케이스가 섞여있음
            // 상황을 구분하고 싶다면 별도 조회로 분기 처리 가능
            throw new IllegalStateException("삭제할 라디오가 없거나 권한이 없습니다.");
        }

        return new FanRadioDeleteResponse(radioId, true);
    }

    @Override
    public List<DriverNumberListResponse> getDriverNumberList() {
        List<DriverNumberProjection> driverNumberList = fanRadioRepository.getDriverNumberList();

        return driverNumberList.stream().map(
                p -> new DriverNumberListResponse(
                        p.getRadioSn(), p.getRadioNum(), p.getRadioNickname(), p.getRadioTextEng(), p.getRadioTextKor()
                ))
                .toList();
    }

    @Override
    public FanRadioResponse getRadioByRadioSn(Long radioSn) {
        FanRadio fanRadio = fanRadioRepository.findById(radioSn)
                .orElseThrow(() -> new BoxboxException(ErrorCode.RADIO_NOT_FOUND));

        return FanRadioResponse.from(fanRadio);
    }
}
