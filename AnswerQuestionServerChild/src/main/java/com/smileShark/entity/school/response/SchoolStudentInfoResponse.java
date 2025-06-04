package com.smileShark.entity.school.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolStudentInfoResponse {
    private StuInfo stuInfo;
    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StuInfo{
        private String studentID;
        private String studentName;
        private String cardID;
        private Boolean sex;
        private String formSchool;
        private String classID;
        private String className;
        private String headTeacherID;
        private String headTeacherName;
        private String levelID;
        private String levelName;
        private String schoolID;
        private String schoolName;
    }
}
