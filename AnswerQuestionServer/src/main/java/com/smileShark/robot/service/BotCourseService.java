package com.smileShark.robot.service;

import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.smileShark.common.response.FinishQuestionCount;
import com.smileShark.constant.Constant;
import com.smileShark.entity.Chapter;
import com.smileShark.entity.Course;
import com.smileShark.entity.Subsection;
import com.smileShark.entity.robot.RobotExam;
import com.smileShark.service.ChapterService;
import com.smileShark.service.CourseService;
import com.smileShark.service.QuestionAndAnswerService;
import com.smileShark.service.SubsectionService;
import com.smileShark.utils.RedisKeyUtil;
import com.smileShark.utils.RedisLockUtil;
import com.smileShark.utils.SearchStringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

@Service
@RequiredArgsConstructor
public class BotCourseService {
    private final Constant constant;
    private final CourseService courseService;
    private final RedisLockUtil redisLockUtil;
    private final ChapterService chapterService;
    private final SubsectionService subsectionService;
    private final StringRedisTemplate stringRedisTemplate;
    private final QuestionAndAnswerService questionAndAnswerService;
    public void searchCourse(Bot bot, GroupMessageEvent event, Matcher matcher) {
        // 提取课程信息
        String courseInfo = matcher.group(1);
        List<Course> list = courseService.lambdaQuery().like(Course::getCourseName, SearchStringUtil.handler(courseInfo)).list();
        StringBuilder buffer = new StringBuilder();
        for (int i = 1; i <= list.size(); i++) {
            buffer.append(i).append(". ").append(list.get(i - 1).getCourseName()).append("\n");
            // 将用户的查询记录下来零时存储到redis中
            stringRedisTemplate.opsForValue().set(
                    RedisKeyUtil.getSimpleKey(
                            constant.PROJECT_NAME,
                            constant.SEARCH_COURSE_REDIS_KEY,
                            event.getUserId().toString(), String.valueOf(i)),
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
                        .text("选择课程编号or直接答题，直接答题将会完成所有课程")
                        .build(),
                false
        );
    }

    public void selectCourseChild(Bot bot, GroupMessageEvent event, String courseSearchLock) {
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
}
