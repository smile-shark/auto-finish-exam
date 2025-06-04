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
public class SchoolStudentHaveCourseResponse {
    private List<Course> completeList;
    private List<Course> unfinished;

    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Course {
        private String courseID;
        private String courseName;
        private Integer totalKonwledge;
        private Integer completeCount;
        private Integer passCount;
    }
}
