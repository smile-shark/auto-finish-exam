package com.smileShark.robot.interceptor;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.exception.ShiroException;
import com.smileShark.entity.User;
import com.smileShark.service.UserService;
import com.smileShark.utils.GlobalBotUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotInterceptor implements BotMessageEventInterceptor {
    private final GlobalBotUtil globalBotUtil;
    private final UserService userService;

    @Override
    public boolean preHandle(Bot bot, MessageEvent event) throws ShiroException {
        if (event.getMessageType().equals("group")) {
            // 群聊验证
            if (globalBotUtil.groupIdVerify((GroupMessageEvent) event)) {
                // 账户绑定验证
                if (event.getRawMessage().length() == 4 ||  userService.lambdaQuery().eq(User::getQqAccount, event.getUserId()).one()!= null) {
                    return true;
                } else {
                    bot.sendGroupMsg(
                            ((GroupMessageEvent) event).getGroupId(),
                            MsgUtils.builder().
                                    at(event.getUserId())
                                    .text("该账户还未绑定，请登录官网绑定账户")
                                    .build(),
                            false
                    );
                }
            }
        }
        // 拦截所有私聊消息
        return false;
    }

    @Override
    public void afterCompletion(Bot bot, MessageEvent event) throws ShiroException {

    }
}
