package com.showrun.boxbox.handler;

import com.showrun.boxbox.exception.BoxboxErrorResponse;
import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger("ErrorLogger");

    @ExceptionHandler(BoxboxException.class)
    public ResponseEntity<BoxboxErrorResponse> handleBase(BoxboxException e, HttpServletRequest req) {
        var ec = e.getErrorCode();
        logError(req, ec.getCode(), e.getMessage(), e);
        return ResponseEntity.status(ec.getStatus())
                .body(BoxboxErrorResponse.of(ec));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BoxboxErrorResponse> handleBadRequest(BadRequestException e, HttpServletRequest req) {
        var ec = ErrorCode.BAD_REQUEST;
        logWarn(req, ec.getCode(), e.getMessage());
        return ResponseEntity.status(ec.getStatus())
                .body(BoxboxErrorResponse.of(ec));
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<BoxboxErrorResponse> handleNotFound(ChangeSetPersister.NotFoundException e, HttpServletRequest req) {
        var ec = ErrorCode.NOT_FOUND;
        logWarn(req, ec.getCode(), e.getMessage());
        return ResponseEntity.status(ec.getStatus())
                .body(BoxboxErrorResponse.of(ec));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BoxboxErrorResponse> handleEtc(Exception e, HttpServletRequest req) {
        var ec = ErrorCode.INTERNAL_ERROR;
        logError(req, ec.getCode(), e.getMessage(), e);
        return ResponseEntity.status(ec.getStatus())
                .body(BoxboxErrorResponse.of(ec));
    }

    private void logWarn(HttpServletRequest req, String code, String msg) {
        log.warn("[WARN] {} {} | code={} | msg={}", req.getMethod(), req.getRequestURI(), code, msg);
    }

    private void logError(HttpServletRequest req, String code, String msg, Exception e) {
        log.error("[ERROR] {} {} | code={} | msg={}", req.getMethod(), req.getRequestURI(), code, msg, e);
    }
}
