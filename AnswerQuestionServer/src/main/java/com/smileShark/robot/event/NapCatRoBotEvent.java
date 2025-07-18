package com.smileShark.robot.event;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.CoreEvent;
import com.smileShark.utils.GlobalBotUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class NapCatRoBotEvent extends CoreEvent {

    @Override
    public void online(Bot bot) {
        GlobalBotUtil.bot = bot;
    }

    @Override
    public void offline(long account) {
        System.out.println("offline");
        System.out.println("account = " + account);
    }
}
