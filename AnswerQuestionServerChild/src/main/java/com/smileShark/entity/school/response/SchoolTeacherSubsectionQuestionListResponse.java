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
public class SchoolTeacherSubsectionQuestionListResponse {
    private String questionID;
    private String questionTitle;
    private List<Answer> answerList;

    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {
        private String answerID;
        private String oppentionContent;
    }
}
