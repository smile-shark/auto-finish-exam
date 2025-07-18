package com.smileShark.entity.robot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class RobotExam {
    private String qqAccount;
    private String courseId;
    private String chapterId;
    private String subsectionId;
    private String userId;
    private String userPassword;
}
