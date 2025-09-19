package com.showrun.boxbox.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class BoxboxErrorResponse {
    private final String code;
    private final String message;
    private final Integer status;

    public static BoxboxErrorResponse of(ErrorCode ec) {
        return BoxboxErrorResponse.builder()
                .code(ec.getCode())
                .message(ec.getMessage())
                .status(ec.getStatus().value())
                .build();
    }
}
