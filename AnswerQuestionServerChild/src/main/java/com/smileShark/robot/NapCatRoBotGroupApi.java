package com.smileShark.robot;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.smileShark.common.Result;
import com.smileShark.common.response.FinishQuestionCount;
import com.smileShark.constant.Constant;
import com.smileShark.entity.Chapter;
import com.smileShark.entity.Course;
import com.smileShark.entity.Subsection;
import com.smileShark.entity.User;
import com.smileShark.entity.news163.News163OutResponse;
import com.smileShark.entity.robot.RobotExam;
import com.smileShark.service.*;
import com.smileShark.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Shiro
@Component
@RequiredArgsConstructor
public class NapCatRoBotGroupApi {
    private final GlobalBotUtil globalBotUtil;
    private final EarlyMorningReportUtil earlyMorningReportUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final Constant constant;
    private final UserService userService;
    private final RedisLockUtil redisLockUtil;
    private final CourseService courseService;
    private final ChapterService chapterService;
    private final SubsectionService subsectionService;
    private final QuestionAndAnswerService questionAndAnswerService;

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "help", at = AtEnum.NEED)
    public void onHelp(Bot bot, GroupMessageEvent event) {
        if (globalBotUtil.groupIdVerify(event)) return;
        StringBuffer buffer = new StringBuffer();
        buffer.append("帮助菜单：\n");
        buffer.append("1. help：查看帮助菜单\n");
        buffer.append("2. 早报：查看早报内容\n");
        buffer.append("3. 早报-天数：查看指定天数的早报内容\n");
        buffer.append("4. 搜索课程-xxx：搜索课程，根据选项自动进行评估考试\n");
        // 返回一些对应的用法示例
        bot.sendGroupMsg(event.getGroupId(), buffer.toString(), false);
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "早报", at = AtEnum.NEED)
    public void earlyMorningReport(Bot bot, GroupMessageEvent event) {
        if (globalBotUtil.groupIdVerify(event)) return;
        try {
            // 返回早报内容
            News163OutResponse report = earlyMorningReportUtil.getEarlyMorningReport(0);
            if (report == null) {
                throw new Exception("获取早报失败");
            }
            bot.sendGroupMsg(
                    event.getGroupId(),
                    String.join("\n", report.getAllData()),
                    false
            );
        } catch (Exception e) {
            log.error("获取早报失败", e);
            bot.sendGroupMsg(
                    event.getGroupId(),
                    "获取早报失败",
                    false
            );
        }
    }

    @GroupMessageHandler
    @MessageHandlerFilter(startWith = "早报-", at = AtEnum.NEED)
    public void earlyMorningReportByDayNum(Bot bot, GroupMessageEvent event) {
        if (globalBotUtil.groupIdVerify(event)) return;
        try {
            // 获取完整消息并去除@部分
            String dayStr = globalBotUtil.messageExtract("早报-", event.getMessage());

            int days = Integer.parseInt(dayStr);
            News163OutResponse report = earlyMorningReportUtil.getEarlyMorningReport(days);

            if (report == null) {
                throw new Exception("获取早报失败");
            }

            bot.sendGroupMsg(
                    event.getGroupId(),
                    MsgUtils.builder()
                            .at(event.getUserId())
                            .text("为您获取最近" + days + "天早报：\n")
                            .text(String.join("\n", report.getAllData()))
                            .build(),
                    false
            );
        } catch (NumberFormatException e) {
            bot.sendGroupMsg(event.getGroupId(), "请输入有效的天数数字", false);
        } catch (Exception e) {
            log.error("获取早报失败", e);
            bot.sendGroupMsg(
                    event.getGroupId(),
                    "获取早报失败",
                    false
            );
        }
    }

    @GroupMessageHandler
    @MessageHandlerFilter(startWith = "搜索课程-")
    public void searchCourse(Bot bot, GroupMessageEvent event) {
        if (globalBotUtil.groupIdVerify(event)) return;
        // 提取课程信息
        String courseInfo = globalBotUtil.messageExtract("搜索课程-", event.getMessage());
        List<Course> list = courseService.lambdaQuery().like(Course::getCourseName, SearchStringUtil.handler(courseInfo)).list();
        StringBuilder buffer = new StringBuilder();
        for (int i = 1; i <= list.size(); i++) {
            buffer.append(i).append(". ").append(list.get(i - 1).getCourseName()).append("\n");
            // 将用户的查询记录下来零时存储到redis中
            stringRedisTemplate.opsForValue().set(
                    RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "searchCourse", event.getUserId().toString(), String.valueOf(i)),
                    list.get(i - 1).getCourseId(),
                    10,
                    TimeUnit.MINUTES
            );
        }
        // 将查询用户信息记录下来
        RobotExam robotExam = new RobotExam();
        robotExam.setQqAccount(event.getUserId().toString());
        redisLockUtil.setCourseSearchLock(event.getUserId().toString(), JSONUtil.toJsonStr(robotExam));
        // 构建消息发送回去
        bot.sendGroupMsg(
                event.getGroupId(),
                MsgUtils.builder()
                        .at(event.getUserId())
                        .text("词条搜索结果：\n")
                        .text(buffer.toString())
                        .text("选择课程编号or直接答题")
                        .build(),
                false
        );
    }

    // 选择章节
    @GroupMessageHandler
    public void selectCourseChild(Bot bot, GroupMessageEvent event) {
        // 群聊验证
        if (globalBotUtil.groupIdVerify(event)) return;
        // 获取课程搜索锁
        String courseSearchLock = redisLockUtil.getCourseSearchLock(event.getUserId().toString());
        if (courseSearchLock == null) return;
        RobotExam examInfo = JSONUtil.toBean(courseSearchLock, RobotExam.class);
        if (event.getMessage().equals("直接答题")) {
            // 开始考试
            bot.sendGroupMsg(
                    event.getGroupId(),
                    MsgUtils.builder()
                            .at(event.getUserId())
                            .text("开始考试")
                            .build(),
                    false
            );
            // 考试实现
            FinishQuestionCount finishQuestionCount = questionAndAnswerService.reBotFinishNormalExam(examInfo);
            // 返回考试结束的数据
            bot.sendGroupMsg(
                    event.getGroupId(),
                    MsgUtils.builder()
                            .at(event.getUserId())
                            .text("考试结束，答对" + finishQuestionCount.getRightAnswerCount() + "道题，答错" + finishQuestionCount.getNoAnswerCount() + "道题，共" + finishQuestionCount.getTotalCount() + "道题")
                            .build(),
                    false
            );
            return;
        }
        // TODO 如果已经有了chapterId，说明已经选择了章节，那么就是小节选择
        if (examInfo.getChapterId() != null) {
            // 查询小节信息
            String subsectionId = stringRedisTemplate.opsForValue().get(
                    RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "searchSubsection", event.getUserId().toString(), event.getMessage())
            );
            if (subsectionId == null) return;
            // 开始考试
            bot.sendGroupMsg(
                    event.getGroupId(),
                    MsgUtils.builder()
                            .at(event.getUserId())
                            .text("开始考试")
                            .build(),
                    false
            );
            // 整合一下考试信息
            examInfo.setSubsectionId(subsectionId);
            // 考试实现
            FinishQuestionCount finishQuestionCount = questionAndAnswerService.reBotFinishNormalExam(examInfo);
            // 返回考试结束的数据
            bot.sendGroupMsg(
                    event.getGroupId(),
                    MsgUtils.builder()
                            .at(event.getUserId())
                            .text("考试结束，答对" + finishQuestionCount.getRightAnswerCount() + "道题，答错" + finishQuestionCount.getNoAnswerCount() + "道题，共" + finishQuestionCount.getTotalCount() + "道题")
                            .build(),
                    false
            );

            return;
        }
        // TODO 构建消息发送回去，怎么分辨用户选择的时章节还是小节，如果课程不为空就是选择的章节
        if (examInfo.getCourseId() != null) {
            // 查询章节信息
            String chapterId = stringRedisTemplate.opsForValue().get(
                    RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "searchChapter", event.getUserId().toString(), event.getMessage())
            );
            if (chapterId == null) return;
            // 查询小节信息
            List<Subsection> subsections = (List<Subsection>) subsectionService.selectSubsectionByChapterId(chapterId).getData();
            StringBuilder buffer = new StringBuilder();
            for (int i = 1; i <= subsections.size(); i++) {
                buffer.append(i).append(". ").append(subsections.get(i - 1).getSubsectionName()).append("\n");
                // 将用户的查询记录下来零时存储到redis中
                stringRedisTemplate.opsForValue().set(
                        RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "searchSubsection", event.getUserId().toString(), String.valueOf(i)),
                        subsections.get(i - 1).getSubsectionId(),
                        10,
                        TimeUnit.MINUTES
                );
            }
            // 构建消息发送回去
            bot.sendGroupMsg(
                    event.getGroupId(),
                    MsgUtils.builder()
                            .at(event.getUserId())
                            .text("章节对应小节列表：\n")
                            .text(buffer.toString())
                            .text("选择小节or直接答题")
                            .build(),
                    false
            );
            // 将查询用户信息记录下来
            examInfo.setChapterId(chapterId);
            redisLockUtil.setCourseSearchLock(event.getUserId().toString(), JSONUtil.toJsonStr(examInfo));
            return;
        }
        // 查询Redis中这个用户存储的课程编号
        String courseId = stringRedisTemplate.opsForValue().get(
                RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "searchCourse", event.getUserId().toString(), event.getMessage())
        );
        if (courseId == null) return;
        // 查询章节信息
        List<Chapter> chapterList = (List<Chapter>) chapterService.selectChapterByCourseId(courseId).getData();
        StringBuilder buffer = new StringBuilder();
        for (int i = 1; i <= chapterList.size(); i++) {
            buffer.append(i).append(". ").append(chapterList.get(i - 1).getChapterName()).append("\n");
            // 将用户的查询记录下来零时存储到redis中
            stringRedisTemplate.opsForValue().set(
                    RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "searchChapter", event.getUserId().toString(), String.valueOf(i)),
                    chapterList.get(i - 1).getChapterId(),
                    10,
                    TimeUnit.MINUTES
            );
        }
        // 构建消息发送回去
        bot.sendGroupMsg(
                event.getGroupId(),
                MsgUtils.builder()
                        .at(event.getUserId())
                        .text("课程对应章节列表：\n")
                        .text(buffer.toString())
                        .text("选择章节or直接答题")
                        .build(),
                false
        );
        // 将查询用户信息记录下来
        examInfo.setCourseId(courseId);
        redisLockUtil.setCourseSearchLock(event.getUserId().toString(), JSONUtil.toJsonStr(examInfo));
    }


    @GroupMessageHandler
    public void verifyUserCode(Bot bot, GroupMessageEvent event) {
        if (globalBotUtil.groupIdVerify(event)) return;
        // 先获取验证的锁，失败则直接返回
        if (!redisLockUtil.getUserCodeLock()) return;

        // 如果这个消息长度是4
        if (event.getMessage().length() == 4) {
            // 尝试在redis中去匹配这个验证码
            String account = stringRedisTemplate.opsForValue().get(
                    RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "verifyCode", "code", event.getMessage())
            );
            if (account != null) {
                // 先去数据库中看看这个发送验证码的qq是否已经绑定过了，如果绑定过了但是和这个找到的账号不一样，就不能通过
                User user = userService.lambdaQuery().eq(User::getQqAccount, event.getUserId().toString()).one();
                if(user != null && !user.getUserId().equals(account)){
                    bot.sendGroupMsg(
                            event.getGroupId(),
                            MsgUtils.builder()
                                    .at(event.getUserId())
                                    .text("该QQ号已经绑定过其他账号")
                                    .build(),
                            false
                    );
                    return;
                }

                // 说明验证成功，将redis中对应的数据删除
                stringRedisTemplate.delete(
                        RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "verifyCode", "code", event.getMessage())
                );
                // 发送成功消息
                bot.sendGroupMsg(
                        event.getGroupId(),
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text("身份验证成功！")
                                .build(),
                        false
                );
                // 将用户的qq号存入数据库中
                userService.saveOrUpdate(new User() {{
                    setQqAccount(String.valueOf(event.getUserId()));
                    setUserId(account);
                }});
            }
        }
    }


}
