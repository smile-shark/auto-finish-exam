package com.smileShark.common;

import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@Component
public class Result {
    private Integer code;
    private Boolean success;
    private String message;
    private Object data;
    private Long time = System.currentTimeMillis();

    public static Result error() {
        return new Result() {{
            setCode(500);
            setSuccess(false);
            setMessage("error");
        }};
    }
    public static Result error(String message) {
        return new Result() {{
            setCode(500);
            setSuccess(false);
            setMessage(message);
        }};
    }

    public static Result success() {
        return new Result() {{
            setCode(200);
            setSuccess(true);
            setMessage("success");
        }};
    }

    public static Result success(String message) {
        return new Result() {{
            setCode(200);
            setSuccess(true);
            setMessage(message);
        }};
    }

    public static Result success(Object o) {
        return new Result() {{
            setCode(200);
            setSuccess(true);
            setMessage("success");
            setData(o);
        }};
    }

    public static Result success(String message, Object data) {
        return new Result() {
            {
                setCode(200);
                setSuccess(true);
                setMessage(message);
                setData(data);
            }
        };
    }

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Result setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public Result setTime(Long time) {
        this.time = time;
        return this;
    }
}
