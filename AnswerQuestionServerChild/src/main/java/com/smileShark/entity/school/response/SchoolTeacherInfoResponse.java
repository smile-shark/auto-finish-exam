package com.smileShark.entity.school.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolTeacherInfoResponse {
    private String accountID;
    private String accountName;
    private String userName;
    private String picture;
    private String cardID;
    private String tel;
    private Integer deptID;
    private String deptName;
    private Boolean sex;
    private String nation;
    @JsonProperty("native") // 使用注解指定JSON字段
    private String nativePlace;
    private String major;
    private String education;
    private String joinTime;
}
