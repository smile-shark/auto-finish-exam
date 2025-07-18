package com.smileShark.entity.school;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolResult<T> {
    private Integer code;
    private T data;
    private Boolean success;
    private LocalDateTime time;
    private String apd;
    private String msg;
}
