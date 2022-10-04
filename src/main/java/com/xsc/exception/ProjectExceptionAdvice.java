package com.xsc.exception;

import com.xsc.protocol.Code;
import com.xsc.protocol.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {
    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException e) {
        return new Result(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException e) {
        return new Result(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result doException(Exception e) {
        return new Result(Code.UNKNOWN_ERR, "系统繁忙，请联系管理员");
    }
}
