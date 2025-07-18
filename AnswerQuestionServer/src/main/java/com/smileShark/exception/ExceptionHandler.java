package com.smileShark.exception;

import com.smileShark.common.Result;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.error();
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public Result handleException(BusinessException e) {
        if(e.getCode() == null){
            return Result.error().setMessage(e.getMsg());
        }
        return Result.error().setMessage(e.getMsg()).setCode(e.getCode());
    }
}
