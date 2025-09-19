package com.showrun.boxbox.exception;

import org.springframework.core.NestedRuntimeException;

public class BoxboxException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BoxboxException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected BoxboxException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    protected BoxboxException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
