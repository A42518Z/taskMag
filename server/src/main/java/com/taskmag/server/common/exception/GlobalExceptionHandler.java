package com.taskmag.server.common.exception;

import com.taskmag.server.common.api.CommonResult;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public CommonResult<Void> handleBiz(BizException e) {
        return CommonResult.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public CommonResult<Void> handleBadRequest(Exception e) {
        return CommonResult.fail(4000, "request param invalid: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<Void> handleOther(Exception e) {
        return CommonResult.fail(5000, "internal server error: " + e.getMessage());
    }
}
