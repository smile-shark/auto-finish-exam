package com.smileShark.robot.service;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotChatService {
    private final ChatClient chatClient;

    public void chatWithCustomers(Bot bot, GroupMessageEvent event, String message) {
        bot.sendGroupMsg(
                event.getGroupId(),
                MsgUtils.builder()
                        .text(chatClient.prompt().user(message).call().content())
                        .build(),
                false
        );
    }
}
