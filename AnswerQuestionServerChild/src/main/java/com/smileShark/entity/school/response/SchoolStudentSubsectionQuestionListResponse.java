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
public class SchoolStudentSubsectionQuestionListResponse {
    private String knowledgeID;
    private Integer questionSumCount;
    private List<Question> questionList;

    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Question {
        private String id;
        private String questionTitle;
        private Integer questionsType;
        private List<Answer> answerList;
    }

    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {
        private String id;
        private String oppentionContent;
    }
}
