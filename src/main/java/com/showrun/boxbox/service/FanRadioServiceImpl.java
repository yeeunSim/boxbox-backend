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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FanRadioServiceImpl implements FanRadioService {
    private final FanRadioRepository fanRadioRepository;
    private final UserRepository userRepository;
    private final PapagoService papagoService;

    @Override
    @Transactional
    public FanRadioResponse createRadio(String loginEmail, FanRadioRequest request) {
        User user = userRepository.findByLogin_LoginEmail(loginEmail)
                .orElseThrow(() -> new BoxboxException(ErrorCode.USER_NOT_FOUND));

        try {
            long myActiveCount = fanRadioRepository.countByUser_UserSnAndRadioDeletedYnFalse(user.getUserSn());
            if (myActiveCount >= 3) {
                throw new BoxboxException(ErrorCode.RADIO_CREATE_LIMIT_EXCEEDED);
            }

            var translated = papagoService.translation(request.getLang(), request.getText());
            String textKor = translated.getTranslationKor();
            String textEng = translated.getTranslationEng();

            FanRadio fanRadio = FanRadio.builder()
                    .user(user)
                    .radioNickname(user.getUserNickname())
                    .radioTextKor(textKor)
                    .radioTextEng(textEng)
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
        } catch (BoxboxException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create radio for {}: {}", loginEmail, e.getMessage(), e);
            throw new BoxboxException(ErrorCode.RADIO_CREATE_FAILED, e);
        }
    }

    @Override
    @Transactional
    public FanRadioResponse patchRadio(String loginEmail, Long radioSn, FanRadioPatchRequest request) {
        User user = userRepository.findByLogin_LoginEmail(loginEmail)
                .orElseThrow(() -> new BoxboxException(ErrorCode.USER_NOT_FOUND));

        try {
            FanRadio radio = fanRadioRepository
                    .findByRadioSnAndUser_UserSnAndRadioDeletedYnFalse(radioSn, user.getUserSn())
                    .orElseThrow(() -> new BoxboxException(ErrorCode.RADIO_NOT_FOUND));

            var translated = papagoService.translation(request.getLang(), request.getText());
            String textKor = translated.getTranslationKor();
            String textEng = translated.getTranslationEng();

            radio.update(textKor, textEng); // 더티 체킹으로 반영.

            return new FanRadioResponse(
                    radio.getRadioSn(),
                    radio.getRadioTextKor(),
                    radio.getRadioTextEng(),
                    radio.getUser().getUserNickname()
            );
        } catch (BoxboxException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to patch radio {} by {}: {}", radioSn, loginEmail, e.getMessage(), e);
            throw new BoxboxException(ErrorCode.RADIO_UPDATE_FAILED, e);
        }
    }

    @Override
    @Transactional
    public FanRadioDeleteResponse deleteRadio(String loginEmail, Long radioSn) {
        User user = userRepository.findByLogin_LoginEmail(loginEmail)
                .orElseThrow(() -> new BoxboxException(ErrorCode.USER_NOT_FOUND));

        FanRadio radio = fanRadioRepository.findByRadioSnAndUser_UserSn(radioSn, user.getUserSn())
                .orElseThrow(() -> new BoxboxException(ErrorCode.RADIO_NOT_FOUND));

        if (radio.isRadioDeletedYn()) {
            throw new BoxboxException(ErrorCode.RADIO_ALREADY_DELETE);
        }

        int updated = fanRadioRepository.softDeleteByOwner(radioSn, user.getUserSn()); // 경쟁 조건 방지 쿼리.
        if (updated == 0) {
            throw new BoxboxException(ErrorCode.RADIO_DELETE_FAILED);
        }

        return new FanRadioDeleteResponse(radioSn, true);
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

    @Override
    public Slice<FanRadioRankResponse> getRadios(RadioSortType sort, Pageable pageable) {
        try {
            Slice<FanRadio> slice =
                    (sort == RadioSortType.POPULAR)
                            ? fanRadioRepository.findPopular(pageable)
                            : fanRadioRepository.findLatest(pageable);

            long startRank = (long) pageable.getPageNumber() * pageable.getPageSize() + 1; // 페이지 기준 순위 계산.
            List<FanRadioRankResponse> mapped = new ArrayList<>(slice.getNumberOfElements());
            for (int i = 0; i < slice.getNumberOfElements(); i++) {
                FanRadio f = slice.getContent().get(i);
                mapped.add(FanRadioRankResponse.fromWithRank(f, startRank + i));
            }
            return new SliceImpl<>(mapped, pageable, slice.hasNext());
        } catch (BoxboxException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to read radios (sort={}, pageable={}): {}", sort, pageable, e.getMessage(), e);
            throw new BoxboxException(ErrorCode.RADIO_READ_FAILED, e);
        }
    }

    @Override
    public Slice<FanRadioRankResponse> searchRadios(String nickname, RadioSortType sort, Pageable pageable) {
        try {
            Slice<FanRadio> slice =
                    (sort == RadioSortType.POPULAR)
                            ? fanRadioRepository.searchPopular(nickname, pageable)
                            : fanRadioRepository.searchLatest(nickname, pageable);

            long startRank = (long) pageable.getPageNumber() * pageable.getPageSize() + 1; // 페이지 기준 순위 계산.
            List<FanRadioRankResponse> mapped = new ArrayList<>(slice.getNumberOfElements());
            for (int i = 0; i < slice.getNumberOfElements(); i++) {
                FanRadio f = slice.getContent().get(i);
                mapped.add(FanRadioRankResponse.fromWithRank(f, startRank + i));
            }
            return new SliceImpl<>(mapped, pageable, slice.hasNext());
        } catch (BoxboxException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to search radios (nickname={}, sort={}, pageable={}): {}",
                    nickname, sort, pageable, e.getMessage(), e);
            throw new BoxboxException(ErrorCode.RADIO_READ_FAILED, e);
        }
    }
}