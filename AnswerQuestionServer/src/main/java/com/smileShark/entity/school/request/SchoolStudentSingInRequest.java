package com.smileShark.entity.school.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolStudentSingInRequest {
    private int ASSType; // 1: 日精进 2: 签到 3: 运动打卡
    private String Comment; // 发送内容
    private String ImgJson; // 图片json数据 "[\"UUID\"]"
}
