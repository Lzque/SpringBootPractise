package com.xsc.exception;

// 业务异常，用户的不规范操作造成
public class BusinessException extends RuntimeException {
    private final Integer code;

    public Integer getCode() {
        return code;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(Integer code, String message, Throwable reason) {
        super(message, reason);
        this.code = code;
    }
}
