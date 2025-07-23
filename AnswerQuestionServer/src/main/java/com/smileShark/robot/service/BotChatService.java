package com.smileShark.robot.service;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.smileShark.entity.User;
import com.smileShark.entity.school.response.SchoolLoginResponse;
import com.smileShark.exception.BusinessException;
import com.smileShark.service.AiChatService;
import com.smileShark.service.ExerciseCueWordsService;
import com.smileShark.service.UserService;
import com.smileShark.utils.SignInUtil;
import com.smileShark.utils.ThreadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;

@Service
@RequiredArgsConstructor
public class BotChatService {
    private final ChatClient chatClient;
    private final UserService userService;
    private final AiChatService aiChatService;
    public void chatWithCustomers(Bot bot, GroupMessageEvent event, String message) {
        try {
            bot.sendGroupMsg(
                    event.getGroupId(),
                    MsgUtils.builder()
                            .text(chatClient.prompt().user(message).call().content())
                            .build(),
                    false
            );
        } catch (Exception e) {
            bot.sendGroupMsg(event.getGroupId(), "调用过于频繁", false);
            e.printStackTrace();
        }
    }


    public void dailyProgress(Bot bot, GroupMessageEvent event, Matcher matcher) {
        // 获取到消息
        String message;
        try{
            // 判断消息有效性
            message = matcher.group(1);
            if(message==null){
                bot.sendGroupMsg(event.getGroupId(), "请输入正确的提示词", false);
            }
            ThreadUtils.executorService.execute(()->{
                // 通过QQ号获取到对应的用户账号密码
                User user = userService.lambdaQuery().eq(User::getQqAccount, event.getUserId()).one();
                if(user==null){
                    // 该QQ未绑定账号
                    bot.sendGroupMsg(
                            event.getGroupId(),
                            MsgUtils.builder()
                                    .at(event.getUserId())
                                    .text("请先绑定一个账号")
                                    .build(),
                            false
                    );
                    return;
                }

                try {
                    aiChatService.signInToday(user,message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 返回成功打卡的消息
                bot.sendGroupMsg(
                        event.getGroupId(),
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text("今日打卡、日精进、运动打卡已完成")
                                .build(),
                        false
                );
            });
        } catch (Exception e){
            e.printStackTrace();
            bot.sendGroupMsg(event.getGroupId(), "请输入正确的提示词", false);
        }
    }
}
