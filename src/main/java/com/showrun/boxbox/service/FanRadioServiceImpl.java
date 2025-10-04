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

        long myActiveCount = fanRadioRepository.countByUser_UserSnAndRadioDeletedYnFalse(user.getUserSn());
        if (myActiveCount >= 3) {
            throw new BoxboxException(ErrorCode.RADIO_CREATE_LIMIT_EXCEEDED);
        }

        Translation translated = translateOrThrow(request.getLang(), request.getText());

        FanRadio saved = fanRadioRepository.save(
                FanRadio.builder()
                        .user(user)
                        .radioNickname(user.getUserNickname())
                        .radioTextKor(translated.kor())
                        .radioTextEng(translated.eng())
                        .radioLikeCount(0)
                        .radioDeletedYn(false)
                        .build()
        );

        return new FanRadioResponse(
                saved.getRadioSn(),
                saved.getRadioTextKor(),
                saved.getRadioTextEng(),
                saved.getUser().getUserNickname()
        );
    }

    @Override
    @Transactional
    public FanRadioResponse patchRadio(String loginEmail, Long radioSn, FanRadioPatchRequest request) {
        User user = userRepository.findByLogin_LoginEmail(loginEmail)
                .orElseThrow(() -> new BoxboxException(ErrorCode.USER_NOT_FOUND));

        FanRadio radio = fanRadioRepository
                .findByRadioSnAndUser_UserSnAndRadioDeletedYnFalse(radioSn, user.getUserSn())
                .orElseThrow(() -> new BoxboxException(ErrorCode.RADIO_NOT_FOUND));

        Translation translated = translateOrThrow(request.getLang(), request.getText());
        radio.update(translated.kor(), translated.eng()); // 더티 체킹 반영

        return new FanRadioResponse(
                radio.getRadioSn(),
                radio.getRadioTextKor(),
                radio.getRadioTextEng(),
                radio.getUser().getUserNickname()
        );
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

        int updated = fanRadioRepository.softDeleteByOwner(radioSn, user.getUserSn());
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
        Slice<FanRadio> slice = (sort == RadioSortType.POPULAR)
                ? fanRadioRepository.findPopular(pageable)
                : fanRadioRepository.findLatest(pageable);

        long startRank = (long) pageable.getPageNumber() * pageable.getPageSize() + 1;
        List<FanRadioRankResponse> mapped = new ArrayList<>(slice.getNumberOfElements());
        for (int i = 0; i < slice.getNumberOfElements(); i++) {
            FanRadio f = slice.getContent().get(i);
            mapped.add(FanRadioRankResponse.fromWithRank(f, startRank + i));
        }
        return new SliceImpl<>(mapped, pageable, slice.hasNext());
    }

    @Override
    public Slice<FanRadioRankResponse> searchRadios(String nickname, RadioSortType sort, Pageable pageable) {
        Slice<FanRadio> slice = (sort == RadioSortType.POPULAR)
                ? fanRadioRepository.searchPopular(nickname, pageable)
                : fanRadioRepository.searchLatest(nickname, pageable);

        long startRank = (long) pageable.getPageNumber() * pageable.getPageSize() + 1;
        List<FanRadioRankResponse> mapped = new ArrayList<>(slice.getNumberOfElements());
        for (int i = 0; i < slice.getNumberOfElements(); i++) {
            FanRadio f = slice.getContent().get(i);
            mapped.add(FanRadioRankResponse.fromWithRank(f, startRank + i));
        }
        return new SliceImpl<>(mapped, pageable, slice.hasNext());
    }

    private Translation translateOrThrow(String lang, String text) {
        try {
            var t = papagoService.translation(lang, text);
            return new Translation(t.getTranslationKor(), t.getTranslationEng());
        } catch (RuntimeException e) {
            log.error("Translation failed (lang={}, textLength={}): {}", lang, text != null ? text.length() : 0, e.getMessage(), e);
            throw new BoxboxException(ErrorCode.TRANSLATION_FAILED, e);
        }
    }

    private record Translation(String kor, String eng) {}

    @Override
    public List<FanRadioResponse> getMyRadios(Long userSn) {
        User user = userRepository.findById(userSn)
                .orElseThrow(() -> new IllegalArgumentException("user not found:" + userSn));

        List<FanRadio> radios = fanRadioRepository
                .myAllList(user.getUserNickname());

        return radios.stream()
                .map(r -> new FanRadioResponse(
                        r.getRadioSn(),
                        r.getRadioNickname(),
                        r.getRadioTextKor(),
                        r.getRadioTextEng()
                ))
                .toList();
    }
}