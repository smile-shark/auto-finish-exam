package com.smileShark.service.imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.constant.Constant;
import com.smileShark.entity.Chapter;
import com.smileShark.entity.Course;
import com.smileShark.entity.Subsection;
import com.smileShark.entity.User;
import com.smileShark.entity.school.SchoolResult;
import com.smileShark.entity.school.request.SchoolLoginRequest;
import com.smileShark.entity.school.response.*;
import com.smileShark.exception.BusinessException;
import com.smileShark.interceptor.TokenInterceptor;
import com.smileShark.mapper.UserMapper;
import com.smileShark.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smileShark.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements UserService {

    private final RestTemplateUtil restTemplateUtil;
    private final Constant constant;
    private final TokenUtil tokenUtil;
    @Lazy
    @Autowired
    private QuestionAndAnswerService questionAndAnswerService;
    private SchoolLoginResponse loginToken;
    private CourseService courseService;
    private final StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisLockUtil redisLockUtil;


    @Override
    @Transactional
    public Result login(Request request) {
        User user = new User();
        // 区分登录者的身份 0-学生 1-老师 2-管理员
        switch (request.getIdentity()) {
            case 0:
                // 学生登录
                loginToken = this.studentLogin(request.getUserId(), request.getUserPassword());
                // 学生信息
                SchoolResult<SchoolStudentInfoResponse> schoolResult = restTemplateUtil.get(
                        constant.STUDENT_INFO_URL,
                        HttpMethod.GET,
                        MediaType.APPLICATION_JSON,
                        null,
                        new ParameterizedTypeReference<>() {
                        },
                        TokenUtil.montage(loginToken),
                        null
                );
                SchoolStudentInfoResponse.StuInfo stuInfo = schoolResult.getData().getStuInfo();
                user.setIdentity(request.getIdentity());
                user.setUserId(request.getUserId());
                user.setUserPassword(request.getUserPassword());
                user.setUsername(stuInfo.getStudentName());
                user.setClassName(stuInfo.getClassName());
                user.setClassId(stuInfo.getClassID());
                user.setTeacherId(stuInfo.getHeadTeacherID());
                user.setTeacherName(stuInfo.getHeadTeacherName());
                user.setLevelId(stuInfo.getLevelID());
                user.setLevelName(stuInfo.getLevelName());
                user.setSchoolId(stuInfo.getSchoolID());
                user.setSchoolName(stuInfo.getSchoolName());
                user.setToken(JSONUtil.toJsonStr(loginToken));
                User student = new User();
                BeanUtil.copyProperties(user,student);
                student.setTokenCreateTime(LocalDateTime.now());
                ThreadUtils.executorService.submit(() -> {
                    // TODO 异步保存用户信息到数据库
                    boolean update = saveOrUpdate(student);
                    if (update) {
                        log.info("用户{}更新成功", student.getUsername());
                    }
                    // 不同等待，调用多线程获取该用户拥有的课程信息
                    loginToken = getStudentToken(student.getUserId(), student.getUserPassword());
                    // 获取用户的课程信息
                    SchoolStudentHaveCourseResponse schoolStudentHaveCourse = courseService.getSchoolStudentHaveCourse(loginToken);
                    // 处理课程信息,list整合
                    schoolStudentHaveCourse.getCompleteList().addAll(schoolStudentHaveCourse.getUnfinished());
                    // 处理课程信息
                    List<Future<SchoolStudentCourseInfoResponse>> futures = new ArrayList<>();
                    for (SchoolStudentHaveCourseResponse.Course course : schoolStudentHaveCourse.getCompleteList()) {
                        boolean exists = courseService.lambdaQuery().eq(Course::getCourseId, course.getCourseID()).exists();
                        if(!exists){
                            courseService.save(new Course() {{
                                setCourseId(course.getCourseID());
                                setCourseName(course.getCourseName());
                            }});
                        }
                        // 如果添加成功了就添加章节
                        if (!exists) {
                            log.info("课程{}添加成功,课程id={}", course.getCourseName(), course.getCourseID());
                            // 获取课程的详细信息，这里会发送很多请求，使用多线程从
                            futures.add(ThreadUtils.executorService.submit(() -> courseService.getSchoolStudentCourseInfoByCourseId(loginToken, course.getCourseID())));
                            // 课程添加成功说明数据库中没有这个课程，所以需要去获取这个课程的新答案
                            questionAndAnswerService.updateCourseAnswer(course.getCourseID());
                        }
                    }
                    // 等待所有课程信息获取完成
                    saveDetailCourseInfo(futures);
                });
                break;
            case 1:
                // 老师登录
                loginToken = this.teacherLogin(request.getUserId(), request.getUserPassword());
                // 获取老师个人信息
                SchoolResult<SchoolTeacherInfoResponse> teacherInfoResponse = restTemplateUtil.get(
                        constant.TEACHER_INFO_URL,
                        HttpMethod.GET,
                        MediaType.APPLICATION_JSON,
                        null,
                        new ParameterizedTypeReference<>() {
                        },
                        TokenUtil.montage(loginToken),
                        null
                );
                SchoolTeacherInfoResponse teacherInfo = teacherInfoResponse.getData();
                user.setIdentity(request.getIdentity());
                user.setUserId(teacherInfo.getCardID());
                user.setUserPassword(request.getUserPassword());
                user.setUsername(teacherInfo.getAccountName());
                user.setSchoolName("anotherAccount=" + teacherInfo.getUserName());
                user.setToken(JSONUtil.toJsonStr(loginToken));
                User teacher = new User();
                BeanUtil.copyProperties(user,teacher);
                teacher.setTokenCreateTime(LocalDateTime.now());
                ThreadUtils.executorService.submit(() -> {
                    // TODO 异步保存用户信息到数据库
                    boolean update = saveOrUpdate(teacher);
                    if (update) {
                        log.info("用户{}更新成功", teacher.getUsername());
                    }
                    // 获取课程信息
                    List<SchoolTeacherCourseSimpleInfoResponse> courseList = courseService.getSchoolTeacherCourseSimpleInfoList(loginToken);
                    List<Future<SchoolStudentCourseInfoResponse>> futures = new ArrayList<>();
                    SchoolLoginResponse randomStudentToken = null;
                    for (SchoolTeacherCourseSimpleInfoResponse course : courseList) {
                        boolean exists = courseService.lambdaQuery().eq(Course::getCourseId, course.getECourseID()).exists();
                        if(!exists){
                            courseService.save(new Course() {{
                                setCourseId(course.getECourseID());
                                setCourseName(course.getECourseName());
                                setLessonId(course.getLessonID());
                            }});
                        }
                        // 如果添加成功了就添加章节
                        if (!exists) {
                            log.info("课程{}添加成功,课程id={}", course.getECourseName(), course.getECourseID());
                            // 获取课程的详细信息，这里会发送很多请求，使用多线程从
                            // loginToken需要使用学生的token去获取这些信息，所以需要再数据库中去随机取出一个学生的token
                            // 随机一个学生的账号密码
                            if (randomStudentToken == null) {
                                User randomStudent = lambdaQuery().eq(User::getIdentity, 0).last("ORDER BY RAND() LIMIT 1").one();
                                randomStudentToken = getStudentToken(randomStudent.getUserId(), randomStudent.getUserPassword());
                            }
                            SchoolLoginResponse finalRandomStudentToken = randomStudentToken;
                            futures.add(ThreadUtils.executorService.submit(
                                            () -> courseService.getSchoolStudentCourseInfoByCourseId(finalRandomStudentToken, course.getECourseID())
                                    )
                            );
                            // 课程添加成功说明数据库中没有这个课程，所以需要去获取这个课程的新答案
                            questionAndAnswerService.updateCourseAnswer(course.getECourseID());
                        }
                    }
                    // 等待所有课程信息获取完成
                    saveDetailCourseInfo(futures);
                });
                break;
            case 2:
                // 管理员登录
                User one = lambdaQuery().eq(User::getUserId, request.getUserId()).one();
                if (one == null) {
                    throw new BusinessException("用户不存在", 402);
                } else {
                    if (one.getUserPassword().equals(request.getUserPassword())) {
                        // 身份验证
                        if(one.getIdentity()!=2){
                            throw new BusinessException("权限不足", 401);
                        }
                        // 管理员登录成功
                        BeanUtil.copyProperties(one, user);
                    } else {
                        throw new BusinessException("密码错误", 403);
                    }
                }
                break;
            default:
                return Result.error("请确认客户端身份");
        }
        // 去除密码
        user.setUserPassword(null);
        // 生成token
        String token = tokenUtil.createToken(user);
        return Result.success("登录成功", token);
    }

    @Override
    public SchoolLoginResponse teacherLogin(String account, String password) {
        if (password == null || password.isEmpty()) {
            // 在数据库中去查询密码
            password = lambdaQuery().eq(User::getUserId, account).one().getUserPassword();
        }
        SchoolLoginResponse schoolLoginResponseTeacher = restTemplateUtil.get(
                constant.TEACHER_LOGIN_URL,
                HttpMethod.POST,
                MediaType.APPLICATION_FORM_URLENCODED,
                new SchoolLoginRequest(
                        account,
                        password,
                        constant.TEACHER_CLIENT_ID_PARAM,
                        constant.TEACHER_CLIENT_SECRET_PARAM
                ),
                SchoolLoginResponse.class,
                null,
                null
        );
        if (schoolLoginResponseTeacher == null) {
            throw new BusinessException("登录失败", 401);
        }
        return schoolLoginResponseTeacher;
    }

    @Override
    public SchoolLoginResponse studentLogin(String account, String password) {
        if (password == null || password.isEmpty()) {
            // 在数据库中去查询密码
            password = lambdaQuery().eq(User::getUserId, account).one().getUserPassword();
        }
        SchoolLoginResponse schoolLoginResponse = restTemplateUtil.get(
                constant.STUDENT_LOGIN_URL,
                HttpMethod.POST,
                MediaType.APPLICATION_FORM_URLENCODED,
                new SchoolLoginRequest(
                        account,
                        password,
                        constant.STUDENT_CLIENT_ID_PARAM,
                        constant.STUDENT_CLIENT_SECRET_PARAM
                ),
                SchoolLoginResponse.class,
                null,
                null
        );
        if (schoolLoginResponse == null) {
            throw new BusinessException("登录失败", 401);
        }
        return schoolLoginResponse;
    }

    @Override
    public SchoolLoginResponse getStudentToken(String account, String password) {
        // 首先在数据库钟查询对应的token信息，如果没有则去学校服务器获取token
        // token创建时间要在两个小时之内的才有效
        User user = lambdaQuery().eq(User::getUserId, account)
                .gt(User::getTokenCreateTime, DateUtil.date(System.currentTimeMillis() - 7200000)).one();
        // 如果token没有过期，则直接返回token
        if (user != null) {
            return JSONUtil.toBean(user.getToken(), SchoolLoginResponse.class);
        }
        // 如果token过期，则去学校服务器获取token
        SchoolLoginResponse schoolLoginResponse = this.studentLogin(account, password);
        // 保存token到数据库
        this.saveToken(account, schoolLoginResponse);
        return schoolLoginResponse;
    }

    @Override
    public SchoolLoginResponse getTeacherToken(String account, String password) {
        // 首先在数据库钟查询对应的token信息，如果没有则去学校服务器获取token
        // token创建时间要在两个小时之内的才有效
        User user = lambdaQuery().eq(User::getUserId, account)
                .gt(User::getTokenCreateTime, DateUtil.date(System.currentTimeMillis() - 7200000)).one();
        // 如果token没有过期，则直接返回token
        if (user != null) {
            return JSONUtil.toBean(user.getToken(), SchoolLoginResponse.class);
        }
        // 如果token过期，则去学校服务器获取token
        SchoolLoginResponse schoolLoginResponse = this.teacherLogin(account, password);
        // 保存token到数据库
        this.saveToken(account, schoolLoginResponse);
        return schoolLoginResponse;
    }

    @Override
    public void saveToken(String account, SchoolLoginResponse response) {
        // 保存token到数据库
        lambdaUpdate()
                .eq(User::getUserId, account)
                .set(User::getTokenCreateTime, DateUtil.date())
                .set(User::getToken, JSONUtil.toJsonStr(response))
                .update();
    }

    @Override
    public Result getUserInfo() {
        User user = TokenInterceptor.getUser();
        // 将解析的用户信息返回
        return Result.success("用户信息获取成功", user);
    }

    @Override
    public void verifyAdmin() {
        User user = TokenInterceptor.getUser();
        if (user.getIdentity() != 2) {
            throw new BusinessException("权限不足", 403);
        }
    }

    @Override
    public Result pageList(Integer page, Integer size, String account, String userName, Integer isTest, Integer identity) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(account != null && !account.isEmpty()){
            userLambdaQueryWrapper.eq(User::getUserId, account);
        }
        if(userName != null && !userName.isEmpty()){
            userLambdaQueryWrapper.like(User::getUsername, SearchStringUtil.handler(userName));
        }
        if(isTest != null){
            userLambdaQueryWrapper.eq(User::getIsTest, isTest);
        }
        if(identity != null){
            userLambdaQueryWrapper.eq(User::getIdentity, identity);
        }
        userLambdaQueryWrapper.ne(User::getIdentity, 2);
        return Result.success("获取成功", page(new Page<>(page, size), userLambdaQueryWrapper));
    }

    @Override
    public Result createCode() {
        User user = TokenInterceptor.getUser();
        // 生成验证码
        String verifyCode = VerifyCode.createVerifyCode();
        // 保存验证码到redis，这个码需要在其他端去验证，所以需要分别将account和verifyCode作为key
        stringRedisTemplate.opsForValue().set(
                RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "verifyCode","account", user.getUserId()),
                verifyCode,
                10,
                TimeUnit.MINUTES
        );
        stringRedisTemplate.opsForValue().set(
                RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "verifyCode","code", verifyCode),
                user.getUserId(),
                10,
                TimeUnit.MINUTES
        );
        redisLockUtil.setUserCodeLock();
        return Result.success("验证码获取成功", verifyCode);
    }

    @Override
    public Result verifyCode() {
        User user = TokenInterceptor.getUser();
        // 其他端验证成功后会吧code部分删除，但是保留account部分，如果code为空，account不为空就验证通过
        String  code= stringRedisTemplate.opsForValue().get(
                RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "verifyCode","account", user.getUserId())
        );
        if (code == null) {
            // 账号对应的验证码不会主动删除所以应该是过期了
            throw new BusinessException("验证码已过期", 405);
        }
        // 根据获取到的验证码去获取账号，如果有就说明没有通过验证
        String account = stringRedisTemplate.opsForValue().get(
                RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "verifyCode", "code", code)
        );
        if (account!=null) {
            throw new BusinessException("验证失败", 406);
        }
        // 验证码验证成功，删除redis中的验证码
        stringRedisTemplate.delete(RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "verifyCode","account", user.getUserId()));
        return Result.success("验证通过");
    }

    public void saveDetailCourseInfo(List<Future<SchoolStudentCourseInfoResponse>> futures) {
        try {
            for (Future<SchoolStudentCourseInfoResponse> future : futures) {
                SchoolStudentCourseInfoResponse courseInfo = future.get();
                courseService.saveCourseDetail(courseInfo);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
