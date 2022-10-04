package com.xsc.exception;

// 系统异常，服务器的问题
public class SystemException extends RuntimeException {
    private final Integer code;

    public Integer getCode() {
        return code;
    }

    public SystemException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(Integer code, String message, Throwable reason) {
        super(message, reason);
        this.code = code;
    }
}
