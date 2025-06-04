package com.smileShark.service;

import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.entity.QuestionAndAnswer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smileShark.entity.Subsection;
import com.smileShark.entity.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
public interface QuestionAndAnswerService extends IService<QuestionAndAnswer> {

    Result selectAnswersByQuestion(Request request);

    Result selectNeedAnswerQuestions(Request request) throws ExecutionException, InterruptedException;

    Result finishTeacherCourseTest(Request request);

    Result finishNormalExam(List<Subsection> subsections);

    Result saveAnswer();

    Result saveTeacherTestExamAnswer(String courseId);
    /**
     * 再添加了新的课程后，需要去获取新的课程答案
     */
    void updateCourseAnswer(String courseId);

    void saveAnswer(User user);

    Result finishNormalExam(User user, List<Subsection> subsections);
}
