package com.smileShark.entity.school.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolTeacherSubsectionExamAnswerRequest {
    private String answerID;
    private String questionID;
    private String kpid;
}
