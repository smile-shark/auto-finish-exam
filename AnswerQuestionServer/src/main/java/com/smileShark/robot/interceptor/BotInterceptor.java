package com.smileShark.robot.interceptor;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.exception.ShiroException;
import com.smileShark.utils.GlobalBotUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotInterceptor implements BotMessageEventInterceptor {
    private final GlobalBotUtil globalBotUtil;

    @Override
    public boolean preHandle(Bot bot, MessageEvent event) throws ShiroException {
        if(event.getMessageType().equals("group")){
            // 群聊验证
            return !globalBotUtil.groupIdVerify((GroupMessageEvent) event);
        }
        // 拦截所有私聊消息
        return false;
    }

    @Override
    public void afterCompletion(Bot bot, MessageEvent event) throws ShiroException {

    }
}
