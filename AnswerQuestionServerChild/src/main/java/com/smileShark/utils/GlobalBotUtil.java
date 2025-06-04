package com.smileShark.utils;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.smileShark.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GlobalBotUtil {
    private final Constant constant;
    public static Bot bot;

    /**
     * 判断是否为指定群组
     * @param event
     * @return
     */
    public boolean groupIdVerify(GroupMessageEvent event){
        return Objects.equals(event.getGroupId(), constant.REBOT_HANDLER_GROUPS);
    }
}
