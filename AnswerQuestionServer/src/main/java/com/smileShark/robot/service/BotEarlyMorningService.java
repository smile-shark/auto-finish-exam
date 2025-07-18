package com.smileShark.robot.service;

import cn.hutool.core.convert.Convert;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.smileShark.entity.news163.News163OutResponse;
import com.smileShark.utils.EarlyMorningReportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotEarlyMorningService {
    private final EarlyMorningReportUtil earlyMorningReportUtil;
    public void sendNowReport(Bot bot, Long groupId) {
        try {
            // 返回早报内容
            News163OutResponse report = earlyMorningReportUtil.getEarlyMorningReport(0);
            if (report == null) {
                throw new Exception("获取早报失败");
            }
            bot.sendGroupMsg(
                    groupId,
                    String.join("\n", report.getAllData()),
                    false
            );
        } catch (Exception e) {
            log.error("获取早报失败", e);
            bot.sendGroupMsg(
                    groupId,
                    "获取早报失败",
                    false
            );
        }
    }
    public void sendNowReport(Bot bot, GroupMessageEvent event, Matcher matcher) {
        int days;
        try {
            days = Convert.toInt(matcher.group(1), 0);
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
}
