package com.smileShark.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinishQuestionCount {
    private Integer rightAnswerCount;
    private Integer noAnswerCount;
}
