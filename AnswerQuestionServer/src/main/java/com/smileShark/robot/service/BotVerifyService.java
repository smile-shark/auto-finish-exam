package com.smileShark.robot.service;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.smileShark.constant.Constant;
import com.smileShark.entity.User;
import com.smileShark.service.UserService;
import com.smileShark.utils.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotVerifyService {
    private final Constant constant;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserService userService;
    public void verifyUserCode(Bot bot, GroupMessageEvent event, String message) {
        // 尝试在redis中去匹配这个验证码
        String account = stringRedisTemplate.opsForValue().get(
                RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME,
                        constant.VERIFY_CODE_REDIS_KEY, message)
        );
        if (account != null) {
            // 先去数据库中看看这个发送验证码的qq是否已经绑定过了，如果绑定过了但是和这个找到的账号不一样，就不能通过
            User user = userService.lambdaQuery().eq(User::getQqAccount, event.getUserId().toString()).one();
            if (user != null && !user.getUserId().equals(account)) {
                bot.sendGroupMsg(
                        event.getGroupId(),
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text("该QQ号已经绑定过其他账号")
                                .build(),
                        false
                );
                return;
            }

            // 说明验证成功，将redis中对应的数据删除
            stringRedisTemplate.delete(
                    RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME,
                            constant.VERIFY_CODE_REDIS_KEY, message)
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
