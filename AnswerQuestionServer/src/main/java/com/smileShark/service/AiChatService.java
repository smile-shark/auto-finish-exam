package com.smileShark.service;

import com.smileShark.common.Result;
import com.smileShark.entity.User;

public interface AiChatService {
    void signInToday(User user,String message) throws InterruptedException;

    Result signInTodayWeb(String message);

}
