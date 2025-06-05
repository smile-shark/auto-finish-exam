package com.smileShark.robot;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.smileShark.constant.Constant;
import com.smileShark.entity.news163.News163OutResponse;
import com.smileShark.utils.EarlyMorningReportUtil;
import com.smileShark.utils.GlobalBotUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Slf4j
@Shiro
@Component
@RequiredArgsConstructor
public class NapCatRoBotApi {
    private final Constant constant;
    private final EarlyMorningReportUtil earlyMorningReportUtil;

    @AnyMessageHandler
        public void onAnyMessage(Bot bot, AnyMessageEvent event) {
        // 初始化全局对象，用于定时主动发送消息
        if(GlobalBotUtil.bot == null){
            GlobalBotUtil.bot = bot;
        }
    }
    // 定时任务示例
    @Scheduled(cron = "0 0 7 * * ?")
    public void sendDailyMessage() {
        GlobalBotUtil.bot.sendGroupMsg(
                constant.REBOT_HANDLER_GROUPS,
                "定时消息发送测试",
                false
        );
    }

    // 私聊消息
    @PrivateMessageHandler
    public void onPrivateMessage(Bot bot, PrivateMessageEvent event,Matcher matcher) {

    }
}
