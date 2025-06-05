package com.smileShark.service.imp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.common.response.FinishQuestionCount;
import com.smileShark.common.response.TotalAndData;
import com.smileShark.constant.Constant;
import com.smileShark.entity.Course;
import com.smileShark.entity.QuestionAndAnswer;
import com.smileShark.entity.Subsection;
import com.smileShark.entity.User;
import com.smileShark.entity.robot.RobotExam;
import com.smileShark.entity.school.SchoolResult;
import com.smileShark.entity.school.request.SchoolStudentSubsectionExamAnswerRequest;
import com.smileShark.entity.school.request.SchoolTeacherSubsectionExamAnswerRequest;
import com.smileShark.entity.school.request.SchoolTeacherTestExamAnswerRequest;
import com.smileShark.entity.school.response.*;
import com.smileShark.exception.BusinessException;
import com.smileShark.interceptor.TokenInterceptor;
import com.smileShark.mapper.QuestionAndAnswerMapper;
import com.smileShark.service.CourseService;
import com.smileShark.service.QuestionAndAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smileShark.service.SubsectionService;
import com.smileShark.service.UserService;
import com.smileShark.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionAndAnswerServiceImp extends ServiceImpl<QuestionAndAnswerMapper, QuestionAndAnswer> implements QuestionAndAnswerService {
    private final RestTemplateUtil restTemplateUtil;
    private final UserService userService;
    private final Constant constant;
    private final RedisLockUtil redisLockUtil;
    private SchoolLoginResponse loginTokenInfo;
    private final SubsectionService subsectionService;
    private final CourseService courseService;
    private SchoolStudentCourseInfoResponse courseInfo;
    private final StringRedisTemplate stringRedisTemplate;


    @Override
    public Result selectAnswersByQuestion(Request request) {
        // 处理获得的字符串
        String buffer = SearchStringUtil.handler(request.getQuestion());
        // 字符串的处理放到前端处理，这里直接返回数据
        Page<QuestionAndAnswer> page = lambdaQuery()
                .like(QuestionAndAnswer::getQuestion, buffer)
                .page(new Page<>(request.getIndex(), 10));
        return Result.success("获取答案成功", page);
    }

    /**
     * 获取需要回答的问题
     *
     * @param request 请求参数：identity 身份标识 ，subsectionId 小节id,chapterId 章节id，courseId 课程id
     */
    @Override
    public Result selectNeedAnswerQuestions(Request request) {

        User user = TokenInterceptor.getUser();
        if (user.getIdentity() == 2) {
            user.setUserId(request.getUserId());
            user.setUserPassword(request.getUserPassword());
            user.setIdentity(request.getIdentity());
        }
        List<Subsection> subsections = new CopyOnWriteArrayList<>();
        int examCount = getExamSubsections(user, request.getCourseId(), request.getChapterId(), request.getSubsectionId(), subsections);
        return Result.success("考试信息获取成功", new TotalAndData(examCount, subsections));
    }

    @Override
    public Result finishTeacherCourseTest(Request request) {
        User user = TokenInterceptor.getUser();
        // 记录没有答案的题目数量
        AtomicInteger noAnswerCount = new AtomicInteger();
        // 记录正确答题的题目数量
        AtomicInteger rightAnswerCount = new AtomicInteger();
        // 获取到token
        loginTokenInfo = userService.getTeacherToken(user.getUserId(), user.getUserPassword());
        // 获取考试题目
        SchoolTeacherTestExamResponse data = restTemplateUtil.get(
                constant.TEACHER_TEST_EXAM_START_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<SchoolResult<SchoolTeacherTestExamResponse>>() {
                },
                TokenUtil.montage(loginTokenInfo),
                new HashMap<>() {{
                    put("ECourseId", request.getCourseId());
                    put("LessonID", courseService.lambdaQuery().eq(Course::getCourseId, request.getCourseId()).one().getLessonId());
                }}
        ).getData();
        // 开始考试
        // 在数据库钟查询答案
        List<Future<?>> futures = new ArrayList<>();
        for (SchoolTeacherTestExamResponse.Question question : data.getQuestionList()) {
            Future<?> future = ThreadUtils.executorService.submit(() -> {
                // 查询答案
                QuestionAndAnswer one = findQuestionAndAnswer(question.getQuestionID());
                if (one == null) {
                    noAnswerCount.getAndIncrement();
                    System.out.println("questionId = " + question.getQuestionID() + " 没有找到答案");
                    return null;
                }
                rightAnswerCount.getAndIncrement();
                AnswerFormatUtil answerFormatUtil = SpringContextUtil.getBean(AnswerFormatUtil.class);
                // 提取正确答案
                List<String> list = answerFormatUtil.formatAnswer(one.getAnswers());
                // 提交答案
                return restTemplateUtil.get(
                        constant.TEACHER_TEST_EXAM_ANSWER_QUESTION_URL,
                        HttpMethod.POST,
                        MediaType.APPLICATION_JSON,
                        new SchoolTeacherTestExamAnswerRequest(
                                String.join(",", list),
                                question.getQuestionID(),
                                data.getTeacherCourseExamID()
                        ),
                        SchoolResult.class,
                        TokenUtil.montage(loginTokenInfo),
                        null
                );
            });
            futures.add(future);
        }
        for (Future<?> future : futures) {
            try {
                future.get(); // 等待任务完成
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return Result.error("提交答案时发生错误");
            }
        }
        // 考试结束
        if (!futures.isEmpty()) {
            restTemplateUtil.get(
                    constant.TEACHER_TEST_EXAM_END_URL,
                    HttpMethod.POST,
                    MediaType.APPLICATION_JSON,
                    null,
                    SchoolResult.class,
                    TokenUtil.montage(loginTokenInfo),
                    MapUtil.of("teacherCourseExamID", data.getTeacherCourseExamID())
            );
        }
        return Result.success("考试结束", new FinishQuestionCount(rightAnswerCount.get(), noAnswerCount.get(), null));
    }

    @Override
    public Result finishNormalExam(List<Subsection> subsections) {
        User user = TokenInterceptor.getUser();
        // 记录没有答案的题目数量
        AtomicInteger noAnswerCount = new AtomicInteger();
        // 记录正确答题的题目数量
        AtomicInteger rightAnswerCount = new AtomicInteger();

        if (user.getIdentity() == 0) {
            // 学生的方式回答问题
            loginTokenInfo = userService.getStudentToken(user.getUserId(), user.getUserPassword());
            List<Future<?>> futures = new ArrayList<>();
            for (Subsection subsection : subsections) {
                Future<?> future = ThreadUtils.executorService.submit(() -> {
                    // 考试开始，获取题目
                    SchoolStudentSubsectionQuestionListResponse studentExamInfo = getStudentSubsectionQuestionById(loginTokenInfo, subsection.getSubsectionId());
                    if (studentExamInfo == null) {
                        return null;
                    }
                    // 提交线程认为考试题目
                    List<Future<?>> examFutures = new ArrayList<>();
                    for (SchoolStudentSubsectionQuestionListResponse.Question question : studentExamInfo.getQuestionList()) {
                        Future<?> examFuture = ThreadUtils.executorService.submit(() -> {
                            // 查询答案
                            QuestionAndAnswer one = findQuestionAndAnswer(question.getId());
                            if (one == null) {
                                System.out.println("questionId = " + question.getId() + " 没有找到答案");
                                noAnswerCount.getAndIncrement();
                                // 提交答案
                                return restTemplateUtil.get(
                                        constant.STUDENT_SUBSECTION_ANSWER_QUESTION_URL,
                                        HttpMethod.POST,
                                        MediaType.APPLICATION_JSON,
                                        new SchoolStudentSubsectionExamAnswerRequest(subsection.getSubsectionId(), new ArrayList<>() {{
                                            add(new SchoolStudentSubsectionExamAnswerRequest.Question(question.getId(), "error"));
                                        }}),
                                        SchoolResult.class,
                                        TokenUtil.montage(loginTokenInfo),
                                        null
                                );
                            }
                            rightAnswerCount.getAndIncrement();
                            // 提取正确答案
                            AnswerFormatUtil answerFormatUtil = SpringContextUtil.getBean(AnswerFormatUtil.class);
                            List<String> list = answerFormatUtil.formatAnswer(one.getAnswers());
                            // 提交答案
                            return restTemplateUtil.get(
                                    constant.STUDENT_SUBSECTION_ANSWER_QUESTION_URL,
                                    HttpMethod.POST,
                                    MediaType.APPLICATION_JSON,
                                    new SchoolStudentSubsectionExamAnswerRequest(subsection.getSubsectionId(), new ArrayList<>() {{
                                        add(new SchoolStudentSubsectionExamAnswerRequest.Question(question.getId(), String.join(",", list)));
                                    }}),
                                    SchoolResult.class,
                                    TokenUtil.montage(loginTokenInfo),
                                    null
                            );
                        });
                        examFutures.add(examFuture);
                    }
                    // 等待考试结束
                    for (Future<?> examFuture : examFutures) {
                        try {
                            examFuture.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            throw new BusinessException("考试失败");
                        }
                    }
                    // 考试结束
                    return restTemplateUtil.get(
                            constant.STUDENT_SUBSECTION_EXAM_END_URL,
                            HttpMethod.POST,
                            MediaType.APPLICATION_JSON,
                            new SchoolStudentSubsectionExamAnswerRequest(subsection.getSubsectionId(), new ArrayList<>()),
                            SchoolResult.class,
                            TokenUtil.montage(loginTokenInfo),
                            null
                    );
                });
                futures.add(future);
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw new BusinessException("考试失败");
                }
            }
        } else if (user.getIdentity() == 1) {
            loginTokenInfo = userService.getTeacherToken(user.getUserId(), user.getUserPassword());
            // 教师的方式回答问题
            List<Future<?>> futures = new ArrayList<>();
            for (Subsection subsection : subsections) {
                Future<?> future = ThreadUtils.executorService.submit(
                        () -> {
                            List<SchoolTeacherSubsectionQuestionListResponse> teacherExamInfo = restTemplateUtil.get(
                                    constant.TEACHER_SUBSECTION_EXAM_START_URL,
                                    HttpMethod.GET,
                                    MediaType.APPLICATION_JSON,
                                    null,
                                    new ParameterizedTypeReference<SchoolResult<List<SchoolTeacherSubsectionQuestionListResponse>>>() {
                                    },
                                    TokenUtil.montage(loginTokenInfo),
                                    Map.of("kpId", subsection.getSubsectionId())
                            ).getData();
                            List<SchoolTeacherSubsectionExamAnswerRequest> teacherExamAnswerList = new ArrayList<>();
                            // 在数据库中查询答案
                            for (SchoolTeacherSubsectionQuestionListResponse question : teacherExamInfo) {
                                // 查询答案
                                QuestionAndAnswer one = findQuestionAndAnswer(question.getQuestionID());
                                if (one == null) {
                                    System.out.println("questionId = " + question.getQuestionID() + " 没有找到答案");
                                    noAnswerCount.getAndIncrement();

                                    // 提交答案
                                    restTemplateUtil.get(
                                            constant.STUDENT_SUBSECTION_ANSWER_QUESTION_URL,
                                            HttpMethod.POST,
                                            MediaType.APPLICATION_JSON,
                                            new SchoolStudentSubsectionExamAnswerRequest(subsection.getSubsectionId(), new ArrayList<>() {{
                                                add(new SchoolStudentSubsectionExamAnswerRequest.Question(question.getQuestionID(), "error"));
                                            }}),
                                            SchoolResult.class,
                                            TokenUtil.montage(loginTokenInfo),
                                            null
                                    );
                                    continue;
                                }
                                rightAnswerCount.getAndIncrement();

                                // 提取正确答案
                                AnswerFormatUtil answerFormatUtil = SpringContextUtil.getBean(AnswerFormatUtil.class);
                                List<String> list = answerFormatUtil.formatAnswer(one.getAnswers());
                                // 整合答案
                                teacherExamAnswerList.add(
                                        new SchoolTeacherSubsectionExamAnswerRequest() {{
                                            setAnswerID(String.join(",", list));
                                            setQuestionID(question.getQuestionID());
                                            setKpid(subsection.getSubsectionId());
                                        }}
                                );
                            }
                            // 提交答案
                            return restTemplateUtil.get(
                                    constant.TEACHER_SUBSECTION_EXAM_END_URL,
                                    HttpMethod.POST,
                                    MediaType.APPLICATION_JSON,
                                    teacherExamAnswerList,
                                    SchoolResult.class,
                                    TokenUtil.montage(loginTokenInfo),
                                    null
                            );
                        }
                );
                futures.add(future);
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw new BusinessException("考试失败");
                }
            }
        }
        return Result.success("考试结束", new FinishQuestionCount(rightAnswerCount.get(), noAnswerCount.get(), null));
    }

    /**
     * 抓取答案
     *
     * @return success
     */
    @Override
    public Result saveAnswer() {
        User user = TokenInterceptor.getUser();
        loginTokenInfo = userService.getStudentToken(user.getUserId(), user.getUserPassword());
        // 获取错题集
        List<SchoolStudentMistakesResponse.Question> questions = getQuestions(loginTokenInfo);
        this.saveQuestions(questions);
        return Result.success("存储答案成功");
    }

    /**
     * 保存教师的考试答案
     *
     * @param courseId 课程id
     * @return success
     */
    @Override
    public Result saveTeacherTestExamAnswer(String courseId) {
        List<SchoolTeacherTestExamRecordResponse> testExamIdList = restTemplateUtil.get(
                constant.TEACHER_TEST_EXAM_RECORD_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<SchoolResult<List<SchoolTeacherTestExamRecordResponse>>>() {
                },
                TokenUtil.montage(loginTokenInfo),
                MapUtil.of("ECourseId", courseId)
        ).getData();
        // 多线程处理
        List<Future<?>> futures = new ArrayList<>();
        for (SchoolTeacherTestExamRecordResponse testExamId : testExamIdList) {
            Future<?> future = ThreadUtils.executorService.submit(() -> {
                // 获取对应试卷的详细
                List<SchoolStudentMistakesResponse.Question> testExamInfo = restTemplateUtil.get(
                        constant.TEACHER_TEST_EXAM_FINISH_INFO_URL,
                        HttpMethod.GET,
                        MediaType.APPLICATION_JSON,
                        null,
                        new ParameterizedTypeReference<SchoolResult<List<SchoolStudentMistakesResponse.Question>>>() {
                        },
                        TokenUtil.montage(loginTokenInfo),
                        MapUtil.of("TeacherCourseExamID", testExamId.getId())
                ).getData();
                // 处理答案
                this.saveQuestions(testExamInfo);
                return null;
            });
            futures.add(future);
        }
        try {
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Result.error("存储答案失败");
        }
        return Result.success("存储答案成功");
    }


    @Override
    public void updateCourseAnswer(String courseId) {
        // 1. 找出数据库中的测试账号
        List<User> testUserList = userService.lambdaQuery().eq(User::getIsTest, 1).eq(User::getIdentity, 0).list();
        for (User user : testUserList) {
            System.out.println("课程记录：" + courseId + "考试学生：" + user.getUsername() + "考试开始");
            long startTime = System.currentTimeMillis();
            try {
                // 同一个账户的同一个课程，如果现在已经在考试就不开始，使用Redis
                if (redisLockUtil.tryLock(user.getUserId() + ":" + courseId, 60)) {
                    // 3. 获取考试列表
                    List<Subsection> subsections = selectNeedAnswerQuestions(user, courseId);
                    if (!subsections.isEmpty()) {
                        // 4. 开始考试
                        finishNormalExam(user, subsections);
                        // 考试完成后释放锁
                        redisLockUtil.unlock(user.getUserId() + ":" + courseId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("课程记录：" + courseId + "考试学生：" + user.getUsername() + "考试结束，耗时：" + (endTime - startTime) + "ms");
        }
    }

    @Override
    public void saveAnswer(User user) {
        SchoolLoginResponse studentToken = userService.getStudentToken(user.getUserId(), user.getUserPassword());
        List<SchoolStudentMistakesResponse.Question> questions = getQuestions(studentToken);
        this.saveQuestions(questions);
    }


    /**
     * 答题
     *
     * @param user        用户
     * @param subsections 考试列表
     * @return 错题数量
     */
    public Result finishNormalExam(User user, List<Subsection> subsections) {
        int noAnswerCountOut = 0;
        int rightAnswerCountOut = 0;
        // 学生的方式回答问题
        SchoolLoginResponse studentToken = userService.getStudentToken(user.getUserId(), user.getUserPassword());
        for (int i = 0; i < subsections.size(); i++) {
            int noAnswerCount = 0;
            // 考试开始，获取题目
            SchoolStudentSubsectionQuestionListResponse studentExamInfo = getStudentSubsectionQuestionById(studentToken, subsections.get(i).getSubsectionId());
            // 提交考试题目
            if (studentExamInfo == null) {
                i--;
                continue;
            }
            for (SchoolStudentSubsectionQuestionListResponse.Question question : studentExamInfo.getQuestionList()) {
                // 查询答案
                QuestionAndAnswer one = findQuestionAndAnswer(question.getId());
                if (one == null) {
                    System.out.println("questionId = " + question.getId() + " 没有找到答案");
                    noAnswerCount++;

                    // 提交答案
                    restTemplateUtil.get(
                            constant.STUDENT_SUBSECTION_ANSWER_QUESTION_URL,
                            HttpMethod.POST,
                            MediaType.APPLICATION_JSON,
                            new SchoolStudentSubsectionExamAnswerRequest(subsections.get(i).getSubsectionId(), new ArrayList<>() {{
                                add(new SchoolStudentSubsectionExamAnswerRequest.Question(question.getId(), "error"));
                            }}),
                            SchoolResult.class,
                            TokenUtil.montage(studentToken),
                            null
                    );
                    continue;
                }
                // 提取正确答案
                AnswerFormatUtil answerFormatUtil = SpringContextUtil.getBean(AnswerFormatUtil.class);
                List<String> list = answerFormatUtil.formatAnswer(one.getAnswers());
                // 提交答案
                restTemplateUtil.get(
                        constant.STUDENT_SUBSECTION_ANSWER_QUESTION_URL,
                        HttpMethod.POST,
                        MediaType.APPLICATION_JSON,
                        new SchoolStudentSubsectionExamAnswerRequest(subsections.get(i).getSubsectionId(), new ArrayList<>() {{
                            add(new SchoolStudentSubsectionExamAnswerRequest.Question(question.getId(), String.join(",", list)));
                        }}),
                        SchoolResult.class,
                        TokenUtil.montage(studentToken),
                        null);
            }
            // 考试结束
            restTemplateUtil.get(
                    constant.STUDENT_SUBSECTION_EXAM_END_URL,
                    HttpMethod.POST,
                    MediaType.APPLICATION_JSON,
                    new SchoolStudentSubsectionExamAnswerRequest(subsections.get(i).getSubsectionId(), new ArrayList<>()),
                    SchoolResult.class,
                    TokenUtil.montage(studentToken),
                    null
            );
            // 考试完成后先或指定数量的答案
            if (noAnswerCount > 0) {
                saveAnswer(user, studentExamInfo.getQuestionList().size());
            }
            noAnswerCountOut += noAnswerCount;
            rightAnswerCountOut += studentExamInfo.getQuestionList().size() - noAnswerCount;
        }
        return Result.success("考试完成", new FinishQuestionCount(rightAnswerCountOut, noAnswerCountOut, null));
    }

    @Override
    public FinishQuestionCount reBotFinishNormalExam(RobotExam examInfo) {
        // 查询用户信息
        User user = userService.lambdaQuery().eq(User::getQqAccount, examInfo.getQqAccount()).one();

        TokenInterceptor.setUser(user);
        if (user.getIdentity() == 0) {
            loginTokenInfo = userService.getStudentToken(user.getUserId(), user.getUserPassword());
        } else if (user.getIdentity() == 1) {
            loginTokenInfo = userService.getTeacherToken(user.getUserId(), user.getUserPassword());
        } else {
            return null;
        }

        // 获取考试的列表
        List<Subsection> subsections = new ArrayList<>();
        int examCount = getExamSubsections(user, examInfo.getCourseId(), examInfo.getChapterId(), examInfo.getSubsectionId(), subsections);
        // 开始考试
        FinishQuestionCount countData = (FinishQuestionCount) finishNormalExam(subsections).getData();
        countData.setTotalCount(examCount);
        ThreadUtils.executorService.submit(() -> {
            // 清理缓存在Redis中的数据
            redisLockUtil.clearCourseSearchLock(examInfo.getQqAccount());
            // 如果有错题就去保存答案
            if (countData.getNoAnswerCount() > 0) {
                saveAnswer(user, countData.getTotalCount());
            }
        });
        return countData;
    }

    private void saveAnswer(User user, int size) {
        SchoolLoginResponse userToken = userService.getStudentToken(user.getUserId(), user.getUserPassword());
        List<SchoolStudentMistakesResponse.Question> questions = getQuestions(userToken,size);
        this.saveQuestions(questions);
    }

    private List<SchoolStudentMistakesResponse.Question> getQuestions(SchoolLoginResponse userToken, int size) {

        HashMap<String, Integer> map = MapUtil.of("PageSize", 0);
        map.put("PageIndex", size);
        return restTemplateUtil.get(
                constant.STUDENT_MISTAKES_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<SchoolResult<SchoolStudentMistakesResponse>>() {
                },
                TokenUtil.montage(userToken),
                map
        ).getData().getData();
    }

    private List<Subsection> selectNeedAnswerQuestions(User user, String courseId) {
        List<Subsection> subsections = new CopyOnWriteArrayList<>();
        Integer examCount = 0;
        // 学生登录
        SchoolLoginResponse studentToken = userService.getStudentToken(user.getUserId(), user.getUserPassword());
        // 如果courseId不为空，先获取到courseInfo
        if (courseId != null && !courseId.isEmpty()) {
            // 获取课程详细
            courseInfo = courseService.getSchoolStudentCourseInfoByCourseId(studentToken, courseId);
            if (courseInfo == null) {
                return subsections;
            }
            getIntegerStudent(subsections, examCount, courseInfo);
        }
        return subsections;
    }


    private Integer getIntegerStudent(List<Subsection> subsections, Integer examCount, SchoolStudentCourseInfoResponse courseInfo) {
        for (SchoolStudentCourseInfoResponse.Chapter chapter : courseInfo.getChapters()) {
            for (SchoolStudentCourseInfoResponse.Knowledge knowledge : chapter.getKnowledgeList()) {
                Subsection subsection = getSubsection(knowledge);
                if (subsection != null) {
                    subsections.add(subsection);
                    examCount += knowledge.getExamNum();
                }
            }
        }
        return examCount;
    }

    private Subsection getSubsection(SchoolStudentCourseInfoResponse.Knowledge knowledge) {

        Subsection subsection = null;
        // 判断是否需要考试
        if (knowledge.getTestMemberInfo() == null) {
            // 未考试
            subsection = new Subsection();
            subsection.setSubsectionId(knowledge.getId());
        } else if (knowledge.getTestMemberInfo().getIsPass() == null) {
            // 未考试
            subsection = new Subsection();
            subsection.setSubsectionId(knowledge.getId());
        } else if (!knowledge.getTestMemberInfo().getIsPass()
                && knowledge.getTestMemberInfo().getTimes() < constant.STUDENT_EXAM_MAX_TIMES) {
            // 考试次数还没用完
            subsection = new Subsection();
            subsection.setSubsectionId(knowledge.getId());
        }
        return subsection;
    }

    private Subsection getSubsection(SchoolTeacherCourseInfoResponse.TeacherKP knowledge) {
        Subsection subsection = null;
        // 判断是否需要考试
        if (knowledge.getIsPass() == null) {
            // 未考试
            subsection = new Subsection();
            subsection.setSubsectionId(knowledge.getKpid());
        } else if (!knowledge.getIsPass()) {
            // 未通过
            subsection = new Subsection();
            subsection.setSubsectionId(knowledge.getKpid());
        }
        return subsection;
    }

    private void saveQuestions(List<SchoolStudentMistakesResponse.Question> questions) {
        // 多线程存储
        List<Future<?>> futureQuestions = new ArrayList<>();
        for (SchoolStudentMistakesResponse.Question question : questions) {
            Future<?> futureQuestion = ThreadUtils.executorService.submit(() -> {
                // 查询释放有该问题
                boolean exists = lambdaQuery().eq(QuestionAndAnswer::getQuestionId, question.getQuestionID()).exists();
                if (exists) {
                    System.out.println("该问题已存在:" + question.getQuestionID());
                    return null;
                }

                // 处理答案
                List<String> answerList = getAnswersFormat(question);
                // 如果不存在则插入，存在时不更新
                try {
                    boolean add = save(new QuestionAndAnswer() {{
                        setQuestionId(question.getQuestionID());
                        setQuestion(question.getQuestionTitle());
                        setAnswers(String.join(constant.SSTR, answerList));
                    }});
                    if (add) {
                        System.out.println("更新成功:" + question.getQuestionID());
                    } else {
                        System.out.println("更新失败");
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("重复插入");
                    return null;
                }
            });
            futureQuestions.add(futureQuestion);
        }
        try {
            for (Future<?> futureQuestion : futureQuestions) {
                futureQuestion.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new BusinessException("存储答案失败");
        }
    }

    private static List<String> getAnswersFormat(SchoolStudentMistakesResponse.Question question) {
        List<String> answerList = new ArrayList<>();
        for (SchoolStudentMistakesResponse.Answer answer : question.getAnswerList()) {
            if (answer.getIsTrue()) {
                // 正确答案需要存入id
                answerList.add(answer.getAnswerID());
            } else {
                // 错误答案需要存入"该位置为错误答案"
                answerList.add("该位置为错误答案");
            }
            answerList.add(answer.getAnswerContent());
        }
        return answerList;
    }

    /**
     * 准备考试的时候不需要获取题目，但是需要得到对应章节下面需要考试的小节
     *
     * @param subsectionId 小节id
     * @return 返回对应章节下面需要考试的小节
     */
    private SchoolStudentSubsectionQuestionListResponse getStudentSubsectionQuestionById(SchoolLoginResponse token, String subsectionId) {
        return restTemplateUtil.get(
                constant.STUDENT_SUBSECTION_EXAM_START_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<SchoolResult<SchoolStudentSubsectionQuestionListResponse>>() {
                },
                TokenUtil.montage(token),
                MapUtil.of("kpId", subsectionId)
        ).getData();
    }

    private QuestionAndAnswer findQuestionAndAnswer(String questionId) {
        // 先再redis中去寻找对应的答案
        String json = stringRedisTemplate.opsForValue().get(
                RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "selectAnswer", questionId)
        );
        // 如果有就返回这个答案

        if (json != null) {
            return JSONUtil.toBean(json, QuestionAndAnswer.class);
        }

        // 如果没有就去数据库中查询
        QuestionAndAnswer questionAndAnswer = lambdaQuery().eq(QuestionAndAnswer::getQuestionId, questionId).one();
        // 使用异步将查询到的数据写入到redis中
        ThreadUtils.executorService.submit(() -> stringRedisTemplate.opsForValue().set(
                RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "selectAnswer", questionId),
                JSONUtil.toJsonStr(questionAndAnswer)
        ));
        // 返回得到的数据
        return questionAndAnswer;
    }

    private List<SchoolStudentMistakesResponse.Question> getQuestions(SchoolLoginResponse token) {
        // 先获取到题目的数量，计算需要多少数量的请求才能获取到所有题目
        SchoolStudentMistakesResponse countData = restTemplateUtil.get(
                constant.STUDENT_MISTAKES_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<SchoolResult<SchoolStudentMistakesResponse>>() {
                },
                TokenUtil.montage(token),
                MapUtil.of("PageSize", 0)
        ).getData();
        int pageSize = (int) Math.ceil((double) countData.getDataCount() / constant.MISTAKES_MAX_COUNT);
        // 多线程获取题目
        List<Future<List<SchoolStudentMistakesResponse.Question>>> futures = new ArrayList<>();
        for (int i = 1; i <= pageSize; i++) {
            int finalI = i;
            futures.add(ThreadUtils.executorService.submit(() -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("PageSize", constant.MISTAKES_MAX_COUNT);
                map.put("PageIndex", finalI);
                return restTemplateUtil.get(
                        constant.STUDENT_MISTAKES_URL,
                        HttpMethod.GET,
                        MediaType.APPLICATION_JSON,
                        null,
                        new ParameterizedTypeReference<SchoolResult<SchoolStudentMistakesResponse>>() {
                        },
                        TokenUtil.montage(token),
                        map
                ).getData().getData();
            }));
        }
        List<SchoolStudentMistakesResponse.Question> questions = new CopyOnWriteArrayList<>();
        for (Future<List<SchoolStudentMistakesResponse.Question>> future : futures) {
            try {
                questions.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new BusinessException("获取答案失败");
            }
        }
        return questions;
    }

    private int getExamSubsections(User user, String courseId, String chapterId, String subsectionId, List<Subsection> subsections) {
        Integer examCount = 0;

        // 1. 判断用户的身份
        if (user.getIdentity() == 0) {
            // 学生登录
            loginTokenInfo = userService.getStudentToken(user.getUserId(), user.getUserPassword());
            // 如果courseId不为空，先获取到courseInfo
            if (courseId != null && !courseId.isEmpty()) {
                // 获取课程详细
                courseInfo = courseService.getSchoolStudentCourseInfoByCourseId(loginTokenInfo, courseId);
                System.out.println(user);
                System.out.println(courseId);
                System.out.println(chapterId);
                System.out.println(subsectionId);
                System.out.println(JSONUtil.toJsonStr(courseInfo));
            }
            if (subsectionId != null && !subsectionId.isEmpty()) {
                // 如果 subsectionId 不为空，返回指定小节的所有问题
                for (SchoolStudentCourseInfoResponse.Chapter chapter : courseInfo.getChapters()) {
                    // 先找到章节
                    if (chapter.getId().equals(chapterId)) {
                        for (SchoolStudentCourseInfoResponse.Knowledge knowledge : chapter.getKnowledgeList()) {
                            // 判断指定的小节是否需要考试
                            if (knowledge.getId().equals(subsectionId)) {
                                Subsection subsection = getSubsection(knowledge);
                                if (subsection != null) {
                                    subsections.add(subsection);
                                    examCount += knowledge.getExamNum();
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            } else if (chapterId != null && !chapterId.isEmpty()) {
                // 如果 chapterId 不为空，返回指定章节的所有问题
                for (SchoolStudentCourseInfoResponse.Chapter chapter : courseInfo.getChapters()) {
                    // 判断指定的章节是否需要考试，完成后就退出循环
                    if (chapter.getId().equals(chapterId)) {
                        for (SchoolStudentCourseInfoResponse.Knowledge knowledge : chapter.getKnowledgeList()) {
                            // 判断指定的小节是否需要考试
                            Subsection subsection = getSubsection(knowledge);
                            if (subsection != null) {
                                subsections.add(subsection);
                                examCount += knowledge.getExamNum();
                            }
                        }
                        break;
                    }
                }
            } else if (courseId != null && !courseId.isEmpty()) {
                // 如果 courseId 不为空，返回指定课程的所有问题
                examCount = getIntegerStudent(subsections, examCount, courseInfo);
            } else {
                // 如果 courseId 为空，返回全部课程的所有问题
                SchoolStudentHaveCourseResponse schoolStudentHaveCourse = courseService.getSchoolStudentHaveCourse(loginTokenInfo);
                Collection<SchoolStudentHaveCourseResponse.Course> courses = CollUtil.addAll(schoolStudentHaveCourse.getUnfinished(), schoolStudentHaveCourse.getCompleteList());
                // 获取课程下面需要考试的小节
                List<Future<SchoolStudentCourseInfoResponse>> futures = new ArrayList<>();
                for (SchoolStudentHaveCourseResponse.Course course : courses) {
                    // 判断课程中是否还有需要考试的题目
                    if (course.getPassCount() < course.getTotalKonwledge()) {
                        // 获取详细的课程信息，使用线程池加快获取进度
                        Future<SchoolStudentCourseInfoResponse> future = ThreadUtils.executorService.submit(
                                () -> courseService.getSchoolStudentCourseInfoByCourseId(loginTokenInfo, course.getCourseID()));
                        futures.add(future);
                    }
                }
                try {
                    for (Future<SchoolStudentCourseInfoResponse> future : futures) {
                        SchoolStudentCourseInfoResponse courseInfo = future.get();// 获取结果
                        examCount = getIntegerStudent(subsections, examCount, courseInfo);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else if (user.getIdentity() == 1) {
            // 教师登录
            loginTokenInfo = userService.getTeacherToken(user.getUserId(), user.getUserPassword());
            // 获取到课程信息
            if (courseId == null || courseId.isEmpty()) {
                // 教师的courseId不能为空，为空会将全部课程都回答了
                throw new BusinessException("教师身份，请指定课程");
            }
            SchoolTeacherCourseInfoResponse teacherCourseInfo = courseService.getSchoolTeacherCourseInfoByCourseId(loginTokenInfo, courseId);

            // 判断
            if (subsectionId != null && !subsectionId.isEmpty()) {
                // 如果 subsectionId 不为空，返回指定小节的所有问题
                for (SchoolTeacherCourseInfoResponse.Chapter chapter : teacherCourseInfo.getChapterList()) {
                    if (chapter.getId().equals(chapterId)) {
                        for (SchoolTeacherCourseInfoResponse.TeacherKP teacherKP : chapter.getTeacherKPList()) {
                            if (teacherKP.getKpid().equals(subsectionId)) {
                                Subsection subsection = getSubsection(teacherKP);
                                if (subsection != null) {
                                    subsections.add(subsection);
                                    examCount += 10;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            } else if (chapterId != null && !chapterId.isEmpty()) {
                // 如果 chapterId 不为空，返回指定章节的所有问题
                for (SchoolTeacherCourseInfoResponse.Chapter chapter : teacherCourseInfo.getChapterList()) {
                    if (chapter.getId().equals(chapterId)) {
                        for (SchoolTeacherCourseInfoResponse.TeacherKP teacherKP : chapter.getTeacherKPList()) {
                            Subsection subsection = getSubsection(teacherKP);
                            if (subsection != null) {
                                subsections.add(subsection);
                                examCount += 10;
                            }
                        }
                        break;
                    }
                }
            } else {
                // 如果 courseId 不为空，返回指定课程的所有问题
                for (SchoolTeacherCourseInfoResponse.Chapter chapter : teacherCourseInfo.getChapterList()) {
                    for (SchoolTeacherCourseInfoResponse.TeacherKP teacherKP : chapter.getTeacherKPList()) {
                        Subsection subsection = getSubsection(teacherKP);
                        if (subsection != null) {
                            subsections.add(subsection);
                            examCount += 10;
                        }
                    }
                }
            }

        } else {
            throw new BusinessException("身份错误",401);
        }
        return examCount;
    }
}
