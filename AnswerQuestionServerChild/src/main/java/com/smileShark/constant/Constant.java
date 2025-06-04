package com.smileShark.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class Constant {
    // projectName
    @Value("${spring.application.name}")
    public String PROJECT_NAME;

    // token
    @Value("${key.token.key}")
    public String TOKEN_KEY;
    @Value("${key.token.expiration-time}")
    public Long TOKEN_EXPIRATION_TIME;

    // mysql data split string
    @Value("${mysql.data.split.string}")
    public String SSTR;

    // times
    @Value("${school.times.STUDENT_EXAM_MAX_TIMES}")
    public Integer STUDENT_EXAM_MAX_TIMES;


    // url
    @Value("${school.url.STUDENT_LOGIN_URL}")
    public String STUDENT_LOGIN_URL;
    @Value("${school.url.STUDENT_INFO_URL}")
    public String STUDENT_INFO_URL;
    @Value("${school.url.STUDENT_HAVE_COURSE_URL}")
    public String STUDENT_HAVE_COURSE_URL;
    @Value("${school.url.STUDENT_GET_COURSE_INFO_URL}")
    public String STUDENT_GET_COURSE_INFO_URL;
    @Value("${school.url.STUDENT_SUBSECTION_EXAM_START_URL}")
    public String STUDENT_SUBSECTION_EXAM_START_URL;
    @Value("${school.url.STUDENT_SUBSECTION_ANSWER_QUESTION_URL}")
    public String STUDENT_SUBSECTION_ANSWER_QUESTION_URL;
    @Value("${school.url.STUDENT_SUBSECTION_EXAM_END_URL}")
    public String STUDENT_SUBSECTION_EXAM_END_URL;
    @Value("${school.url.STUDENT_MISTAKES_URL}")
    public String STUDENT_MISTAKES_URL;


    @Value("${school.url.TEACHER_LOGIN_URL}")
    public String TEACHER_LOGIN_URL;
    @Value("${school.url.TEACHER_INFO_URL}")
    public String TEACHER_INFO_URL;
    @Value("${school.url.TEACHER_TEST_EXAM_START_URL}")
    public String TEACHER_TEST_EXAM_START_URL;
    @Value("${school.url.TEACHER_TEST_EXAM_ANSWER_QUESTION_URL}")
    public String TEACHER_TEST_EXAM_ANSWER_QUESTION_URL;
    @Value("${school.url.TEACHER_TEST_EXAM_END_URL}")
    public String TEACHER_TEST_EXAM_END_URL;
    @Value("${school.url.TEACHER_PASS_SUBSECTION_EXAM_URL}")
    public String TEACHER_PASS_SUBSECTION_EXAM_URL;
    @Value("${school.url.TEACHER_SUBSECTION_EXAM_START_URL}")
    public String TEACHER_SUBSECTION_EXAM_START_URL;
    @Value("${school.url.TEACHER_SUBSECTION_EXAM_END_URL}")
    public String TEACHER_SUBSECTION_EXAM_END_URL;
    @Value("${school.url.TEACHER_TEST_EXAM_RECORD_URL}")
    public String TEACHER_TEST_EXAM_RECORD_URL;
    @Value("${school.url.TEACHER_TEST_EXAM_FINISH_INFO_URL}")
    public String TEACHER_TEST_EXAM_FINISH_INFO_URL;
    @Value("${school.url.TEACHER_COURSE_LIST_URL}")
    public String TEACHER_COURSE_LIST_URL;

    // params
    @Value("${school.params.STUDENT_CLIENT_ID}")
    public String STUDENT_CLIENT_ID_PARAM;
    @Value("${school.params.STUDENT_CLIENT_SECRET}")
    public String STUDENT_CLIENT_SECRET_PARAM;
    @Value("${school.params.TEACHER_CLIENT_ID}")
    public String TEACHER_CLIENT_ID_PARAM;
    @Value("${school.params.TEACHER_CLIENT_SECRET}")
    public String TEACHER_CLIENT_SECRET_PARAM;
    @Value("${school.params.MISTAKES_MAX_COUNT}")
    public Integer MISTAKES_MAX_COUNT;
}
