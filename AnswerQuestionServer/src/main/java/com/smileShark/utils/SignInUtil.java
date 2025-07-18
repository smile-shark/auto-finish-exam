package com.smileShark.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.smileShark.constant.Constant;
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

    public void singIn(SchoolLoginResponse studentToken) {
        SchoolStudentSingInRequest request = new SchoolStudentSingInRequest();
        request.setASSType(2);
        System.out.println(restTemplateUtil.get(constant.STUDENT_SING_IN_URL,
                HttpMethod.POST,
                null,
                request,
                String.class,
                TokenUtil.montage(studentToken),
                null
        ));
    }

    public void sendDailyProgress(SchoolLoginResponse studentToken, String dailyContent) {
        System.out.println(restTemplateUtil.get(constant.STUDENT_SING_IN_URL,
                HttpMethod.POST,
                null,
                new SchoolStudentSingInRequest(1, dailyContent, "[]"),
                String.class,
                TokenUtil.montage(studentToken),
                null
        ));
    }

    public void sendSportsSignIn(SchoolLoginResponse studentToken, String motionContent) {
        System.out.println(restTemplateUtil.get(constant.STUDENT_SING_IN_URL,
                HttpMethod.POST,
                null,
                new SchoolStudentSingInRequest(0, motionContent,
                        JSONUtil.toJsonStr(List.of(IdUtil.simpleUUID()))),
                String.class,
                TokenUtil.montage(studentToken),
                null
        ));
    }
}
