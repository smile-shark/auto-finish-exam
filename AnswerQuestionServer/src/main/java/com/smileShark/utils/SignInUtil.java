package com.smileShark.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.smileShark.constant.Constant;
import com.smileShark.entity.school.SchoolResult;
import com.smileShark.entity.school.request.SchoolStudentSingInRequest;
import com.smileShark.entity.school.response.SchoolLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SignInUtil {
    private final Constant constant;
    private final RestTemplateUtil restTemplateUtil;

    public SchoolResult<?> singIn(SchoolLoginResponse studentToken) {
        SchoolStudentSingInRequest request = new SchoolStudentSingInRequest();
        request.setASSType(2);
        return restTemplateUtil.get(constant.STUDENT_SING_IN_URL,
                HttpMethod.POST,
                null,
                request,
                SchoolResult.class,
                TokenUtil.montage(studentToken),
                null
        );
    }

    public SchoolResult<?> sendDailyProgress(SchoolLoginResponse studentToken, String dailyContent) {
        return restTemplateUtil.get(constant.STUDENT_SING_IN_URL,
                HttpMethod.POST,
                null,
                new SchoolStudentSingInRequest(1, dailyContent, "[]"),
                SchoolResult.class,
                TokenUtil.montage(studentToken),
                null
        );
    }

    public SchoolResult<?> sendSportsSignIn(SchoolLoginResponse studentToken, String motionContent) {
        return restTemplateUtil.get(constant.STUDENT_SING_IN_URL,
                HttpMethod.POST,
                null,
                new SchoolStudentSingInRequest(0, motionContent,
                        JSONUtil.toJsonStr(List.of(IdUtil.simpleUUID()))),
                SchoolResult.class,
                TokenUtil.montage(studentToken),
                null
        );
    }
}
