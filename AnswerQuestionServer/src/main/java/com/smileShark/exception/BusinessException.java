package com.smileShark.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException{
    private String msg;
    private Integer code;
    public BusinessException(String msg, Integer code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }
    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
