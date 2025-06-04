package com.smileShark.controller;

import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.entity.Subsection;
import com.smileShark.entity.User;
import com.smileShark.service.QuestionAndAnswerService;
import com.smileShark.service.UserService;
import com.smileShark.service.imp.QuestionAndAnswerServiceImp;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
@RestController
@RequestMapping("/questionAndAnswer")
@RequiredArgsConstructor
public class QuestionAndAnswerController {
    private final QuestionAndAnswerService questionAndAnswerService;
    private final UserService userService;

    // 获取问题列表接口
    @PostMapping("/page-list")
    @SecurityRequirement(name = "bearerAuth")
    public Result selectAnswers(@RequestBody Request request) {
        // 获取到问题以及索引，服务器自设定每页数量，通过index参数确定页数，返回对应页数的答案列表
        return questionAndAnswerService.selectAnswersByQuestion(request);
    }

    // 获取需要回答的题目
    @PostMapping("/need-question-list")
    @SecurityRequirement(name = "bearerAuth")
    public Result selectNeedAnswerQuestions(@RequestBody Request request) throws ExecutionException, InterruptedException {
        return questionAndAnswerService.selectNeedAnswerQuestions(request);
    }

    // 管理员获取需要回答的题目
    @PostMapping("/admin-need-question-list")
    @SecurityRequirement(name = "bearerAuth")
    public Result adminSelectNeedAnswerQuestions(@RequestBody Request request) throws ExecutionException, InterruptedException {
        userService.verifyAdmin();
        return questionAndAnswerService.selectNeedAnswerQuestions(request);
    }

    // 教师课程测评考试
    @PostMapping("/teacher-course-test-exam")
    @SecurityRequirement(name = "bearerAuth")
    public Result finishTeacherCourseTest(@RequestBody Request request){
        return questionAndAnswerService.finishTeacherCourseTest(request);
    }

    // 常规考试完成
    @PostMapping("/normal-exam-finish")
    @SecurityRequirement(name = "bearerAuth")
    public Result finishNormalExam(@RequestBody List<Subsection> subsections){
        return questionAndAnswerService.finishNormalExam(subsections);
    }
    // 管理员主动做常规考试完成
    @PostMapping("/admin-normal-exam-finish")
    @SecurityRequirement(name = "bearerAuth")
    public Result finishNormalExam(@RequestParam String userId,@RequestParam String userPassword,@RequestParam Integer identity,@RequestBody List<Subsection> subsections){
        userService.verifyAdmin();
        return questionAndAnswerService.finishNormalExam(new User(){{
            setUserId(userId);
            setUserPassword(userPassword);
            setIdentity(identity);
        }},subsections);
    }

    // 抓取答案存储到数据库中
    @PostMapping("/save-answer")
    @SecurityRequirement(name = "bearerAuth")
    public Result saveAnswer() {
        return questionAndAnswerService.saveAnswer();
    }

    // 抓取教师的测试答案存储到数据库中
    @PostMapping("/teacher-save-answer")
    @SecurityRequirement(name = "bearerAuth")
    public Result teacherSaveAnswer(@RequestParam String courseId) {
        return questionAndAnswerService.saveTeacherTestExamAnswer(courseId);
    }

    // 获取题目数量
    @GetMapping("/count")
    @SecurityRequirement(name = "bearerAuth")
    public Result getQuestionCount() {
        userService.verifyAdmin();
        return Result.success("查询成功",questionAndAnswerService.lambdaQuery().count());
    }

    // 管理手动保存答案
    @PostMapping("/admin-save-answer")
    @SecurityRequirement(name = "bearerAuth")
    public Result saveManualAnswer(@RequestBody User user) {
        userService.verifyAdmin();
        questionAndAnswerService.saveAnswer(user);
        return Result.success("保存成功");
    }



}
