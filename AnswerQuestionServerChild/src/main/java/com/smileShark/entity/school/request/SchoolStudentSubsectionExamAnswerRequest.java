package com.smileShark.entity.school.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolStudentSubsectionExamAnswerRequest {
    private String kpid;
    private List<Question> questions;

    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Question {
        private String QuestionID;
        private String AnswerID;
    }
}
