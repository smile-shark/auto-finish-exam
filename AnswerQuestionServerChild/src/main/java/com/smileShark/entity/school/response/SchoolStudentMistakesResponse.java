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
public class SchoolStudentMistakesResponse {
    private Integer dataCount;
    private List<Question> data;
    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Question {
        private String questionID;
        private String questionTitle;
        private List<Answer> answerList;
    }

    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {
        private String answerID;
        private String answerContent;
        private Boolean isTrue;
    }
}
