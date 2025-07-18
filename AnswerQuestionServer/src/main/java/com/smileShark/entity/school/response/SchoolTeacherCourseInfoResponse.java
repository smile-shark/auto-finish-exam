package com.smileShark.entity.school.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolTeacherCourseInfoResponse {
    private String courseName;
    private List<Chapter> chapterList;
    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chapter{
        private String id;
        private String title;
        private String titleContent;
        private List<TeacherKP> teacherKPList;
    }
    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherKP{
        private String knowledge;
        private String kpid;
        private Boolean isPass; // true通过，false未通过, null未评价
    }
}
