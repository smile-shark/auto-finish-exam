package com.smileShark.robot;

import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.smileShark.constant.Constant;
import com.smileShark.entity.User;
import com.smileShark.entity.news163.News163OutResponse;
import com.smileShark.service.UserService;
import com.smileShark.utils.EarlyMorningReportUtil;
import com.smileShark.utils.GlobalBotUtil;
import com.smileShark.utils.RedisKeyUtil;
import com.smileShark.utils.RedisLockUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

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

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "help", at = AtEnum.NEED)
    public void onHelp(Bot bot, GroupMessageEvent event) {
        if (!globalBotUtil.groupIdVerify(event)) return;
        StringBuffer buffer = new StringBuffer();
        buffer.append("帮助菜单：\n");
        buffer.append("1. help：查看帮助菜单\n");
        buffer.append("2. 早报：查看早报内容\n");
        buffer.append("2. 早报-天数：查看指定天数的早报内容\n");
        // 返回一些对应的用法示例
        bot.sendGroupMsg(event.getGroupId(), buffer.toString(), false);
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "早报", at = AtEnum.NEED)
    public void earlyMorningReport(Bot bot, GroupMessageEvent event) {
        if (!globalBotUtil.groupIdVerify(event)) return;
        // 设置锁防止频繁访问
        if (!redisLockUtil.tryLock("earlyMorningReport", 60)) {
            bot.sendGroupMsg(
                    event.getGroupId(),
                    "接口冷却中",
                    false
            );
        }
        try {
            // 返回早报内容
            News163OutResponse earlyMorningReport = earlyMorningReportUtil.getEarlyMorningReport();
            bot.sendGroupMsg(
                    event.getGroupId(),
                    String.join("\n", earlyMorningReport.getAllData()),
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
        if (!globalBotUtil.groupIdVerify(event)) return;
        if (!redisLockUtil.tryLock("earlyMorningReport", 60)) {
            bot.sendGroupMsg(
                    event.getGroupId(),
                    "接口冷却中",
                    false
            );
        }
        try {
            // 获取完整消息并去除@部分
            String dayStr = event.getMessage().split("早报-")[1];

            int days = Integer.parseInt(dayStr);
            News163OutResponse report = earlyMorningReportUtil.getEarlyMorningReport(days);

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
    public void verifyUserCode(Bot bot, GroupMessageEvent event) {
        // 如果这个消息长度是4
        if (event.getMessage().length() == 4) {
            // 尝试在redis中去匹配这个验证码
            String account = stringRedisTemplate.opsForValue().get(
                    RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "verifyCode", "code", event.getMessage())
            );
            if (account != null) {
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
