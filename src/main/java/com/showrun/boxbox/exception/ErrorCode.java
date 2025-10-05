package com.showrun.boxbox.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 공통 에러
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON_002", "요청 값이 유효하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_003", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_004", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_005", "리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "COMMON_006", "자원 충돌이 발생했습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_007", "서버 에러가 발생했습니다."),

    // 로그인 관련 에러
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_001", "아이디 또는 비밀번호가 틀렸습니다."),

    // 회원 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USR_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USR_002", "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USR_003", "이미 존재하는 이메일입니다."),

    // 라디오 관련 에러
    RADIO_NOT_FOUND(HttpStatus.NOT_FOUND, "RADIO_001", "해당 라디오를 찾을 수 없습니다."),
    RADIO_CREATE_FAILED(HttpStatus.BAD_REQUEST, "RADIO_002", "라디오 등록에 실패했습니다."),
    RADIO_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "RADIO_003", "라디오 수정에 실패했습니다."),
    RADIO_DELETE_FAILED(HttpStatus.BAD_REQUEST, "RADIO_004", "라디오 삭제에 실패했습니다."),
    RADIO_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RADIO_005", "라디오 조회에 실패했습니다."),
    RADIO_ALREADY_DELETE(HttpStatus.NOT_FOUND, "RADIO_006", "이미 삭제된 라디오입니다."),
    RADIO_CREATE_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "RADIO_007", "라디오는 최대 3개까지 작성할 수 있습니다."),

    // 언어 설정
    LANG_CHANGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LANG_001", "언어 변경에 실패했습니다."),

    // 번역 관련 에러
    TRANSLATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TRN_001", "번역 서비스 호출에 실패했습니다."),

    // 인증 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "만료된 토큰입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
