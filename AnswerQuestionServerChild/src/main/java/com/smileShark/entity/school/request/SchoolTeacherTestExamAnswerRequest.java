package com.smileShark.entity.school.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolTeacherTestExamAnswerRequest {
    private String AnswerStr;
    private String QuestionID;
    private String TeacherCourseExamID;
}
