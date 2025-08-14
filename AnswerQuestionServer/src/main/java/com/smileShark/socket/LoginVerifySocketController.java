package com.smileShark.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LoginVerifySocketController {
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/login")
    public void addUser(@Header String userId, SimpMessageHeaderAccessor headerAccessor){
        if(userId!=null){
            headerAccessor.getSessionAttributes().put("userId", userId);
        }
    }
}
