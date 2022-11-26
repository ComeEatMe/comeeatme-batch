package com.comeeatme.batch.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiRequestErrorException extends RuntimeException {

    private final String errorCode;

    public ApiRequestErrorException(String errorCode, String errorMessage) {
        super("ErrorCode=" + errorCode + ":" + errorMessage);
        this.errorCode = errorCode;
    }
}
