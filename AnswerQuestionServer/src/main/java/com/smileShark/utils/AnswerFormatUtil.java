package com.smileShark.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ReUtil;
import com.smileShark.constant.Constant;
import com.smileShark.entity.school.response.SchoolStudentSubsectionQuestionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnswerFormatUtil {
    private final Constant constant;
    private final SchoolStudentSubsectionQuestionListResponse.Answer answer;

    public List<String> formatAnswer(String answerStr){
        // 分隔符分割
        String[] split = answerStr.split(constant.SSTR);
        return ListUtil.of(split).stream()
                .filter(s -> ReUtil.isMatch("^[a-z0-9]{32}$", s)) // 匹配 32 位由小写字母和数字组成的字符串
                .collect(Collectors.toList());
    }
}
