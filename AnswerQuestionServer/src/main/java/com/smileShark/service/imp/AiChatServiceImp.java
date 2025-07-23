package com.smileShark.service.imp;

import com.smileShark.common.Result;
import com.smileShark.entity.User;
import com.smileShark.entity.school.response.SchoolLoginResponse;
import com.smileShark.exception.BusinessException;
import com.smileShark.interceptor.TokenInterceptor;
import com.smileShark.service.AiChatService;
import com.smileShark.service.ExerciseCueWordsService;
import com.smileShark.service.UserService;
import com.smileShark.utils.SignInUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiChatServiceImp implements AiChatService {

    private final ChatClient chatClient;
    private final UserService userService;
    private final SignInUtil signInUtil;
    private final ExerciseCueWordsService exerciseCueWordsService;

    @Override
    public void signInToday(User user,String message) throws InterruptedException {
        // 获取登录的令牌
        SchoolLoginResponse studentToken = userService.getStudentToken(user.getUserId(), user.getUserPassword());
        // 进行签到
        while (signInUtil.singIn(studentToken).getCode() == -1) {
            System.out.println("签到失败，正在重试...");
            Thread.sleep(2000);
        }

        Thread.sleep(1000);


        // 发送运动打卡
        // 1. 通过ai获取到运动打卡内容
        String motionContent = chatClient.prompt().user(
                exerciseCueWordsService.lambdaQuery().last("order by rand() limit 1").one().getContent()
        ).call().content();
        // 2. 发送运动打卡内容
        while (signInUtil.sendSportsSignIn(studentToken, motionContent).getCode() == -1) {
            System.out.println("运动打卡失败，正在重试...");
            Thread.sleep(2000);
        }

        Thread.sleep(1000);

        // 发送日精进
        // 1. 通过ai获取到日精进内容
        String dailyContent = chatClient.prompt().user("今天我学习了" + message + "请帮我写200字的日精进").call().content();
        // 2. 发送日精进内容
        while (signInUtil.sendDailyProgress(studentToken, dailyContent).getCode() == -1) {
            System.out.println("日精进失败，正在重试...");
            Thread.sleep(2000);
        }

    }

    @Override
    public Result signInTodayWeb(String message) {
        User user = TokenInterceptor.getUser();
        try{
            signInToday(user,message);
        } catch (InterruptedException e) {
            throw new BusinessException("签到失败", 500);
        }
        return Result.success("签到成功");
    }
}
