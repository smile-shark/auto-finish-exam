package com.smileShark.robot.controller;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Order;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.smileShark.constant.Constant;
import com.smileShark.robot.service.BotChatService;
import com.smileShark.robot.service.BotCourseService;
import com.smileShark.robot.service.BotEarlyMorningService;
import com.smileShark.robot.service.BotVerifyService;
import com.smileShark.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

@Slf4j
@Shiro
@Component
@RequiredArgsConstructor
public class BotGroupController {
    private final Constant constant;
    private final GlobalBotUtil globalBotUtil;
    private final RedisLockUtil redisLockUtil;
    private final BotChatService botChatService;
    private final BotCourseService botCourseService;
    private final BotVerifyService botVerifyService;
    private final StringRedisTemplate stringRedisTemplate;
    private final BotEarlyMorningService botEarlyMorningService;

    /**
     * 定时任务每天早上8点发送早报
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void scheduledTask() {
        System.out.println("定时任务执行 - 当前时间: " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        // 早报定时任务
        if (GlobalBotUtil.bot != null) {
            botEarlyMorningService.sendNowReport(GlobalBotUtil.bot, constant.REBOT_HANDLER_GROUPS);
        }
    }
    @Order(1)
    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^help$", at = AtEnum.NEED)
    public void onHelp(Bot bot, GroupMessageEvent event) {
        System.out.println("help");
        if (globalBotUtil.groupIdVerify(event)) return;
        StringBuffer buffer = new StringBuffer();
        buffer.append("帮助菜单：\n");
        buffer.append("1. help：查看帮助菜单\n");
        buffer.append("2. 早报：查看早报内容\n");
        buffer.append("3. 早报-天数：查看指定天数的早报内容\n");
        buffer.append("4. 搜索课程-xxx：搜索课程，根据选项自动进行评估考试\n");
        // 返回一些对应的用法示例
        bot.sendGroupMsg(event.getGroupId(), buffer.toString(), false);
        redisLockUtil.setMessageInterceptorLock();
    }


    @Order(1)
    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^早报$", at = AtEnum.NEED)
    public void earlyMorningReport(Bot bot, GroupMessageEvent event) {
        botEarlyMorningService.sendNowReport(bot, event.getGroupId());
        redisLockUtil.setMessageInterceptorLock();
    }

    @Order(1)
    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^早报\\s(.*)?$", at = AtEnum.NEED)
    public void earlyMorningReportByDayNum(Bot bot, GroupMessageEvent event, Matcher matcher) {
        botEarlyMorningService.sendNowReport(bot, event, matcher);
        redisLockUtil.setMessageInterceptorLock();
    }

    @Order(1)
    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^搜索课程\\s(.*)?$", at = AtEnum.NEED)
    public void searchCourse(Bot bot, GroupMessageEvent event,Matcher matcher) {
        botCourseService.searchCourse(bot, event, matcher);
        redisLockUtil.setMessageInterceptorLock();
    }

    @Order(2)
    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(.*)?$")
    public void globalMessageHandler(Bot bot, GroupMessageEvent event,Matcher matcher) {
        String message = matcher.group(1);
        // 获取课程搜索锁
        String courseSearchLock = redisLockUtil.getCourseSearchLock(event.getUserId().toString());
        if (courseSearchLock != null) {
            // 做题操作
            botCourseService.selectCourseChild(bot, event,courseSearchLock);
            redisLockUtil.setMessageInterceptorLock();
            return;
        }
        // 获取验证的锁
        if (redisLockUtil.getUserCodeLock()&&message.length() == 4){
            // 如果这个消息长度是4
            botVerifyService.verifyUserCode(bot, event,message);
            redisLockUtil.setMessageInterceptorLock();
        }
    }

    @Order(3)
    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(.*)?$",at=AtEnum.NEED)
    public void atMessageGlobalHandler(Bot bot, GroupMessageEvent event, Matcher matcher){
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(constant.MESSAGE_INTERCEPTOR_REDIS_KEY))){
            System.out.println("消息拦截器拦截到消息，不执行");
            stringRedisTemplate.delete(constant.MESSAGE_INTERCEPTOR_REDIS_KEY);
            return;
        };
        String message = matcher.group(1);
        System.out.println("message = " + message);
        botChatService.chatWithCustomers(bot, event, message);
    }


}
