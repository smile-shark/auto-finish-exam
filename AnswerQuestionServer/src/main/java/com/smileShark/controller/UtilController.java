package com.smileShark.controller;

import com.smileShark.common.Result;
import com.smileShark.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/util")
@RequiredArgsConstructor
public class UtilController {
    private final AiChatService aiChatService;
    @PostMapping("/sign-in-today")
    public Result signInToday(@RequestParam("message") String message) {
        return aiChatService.signInTodayWeb(message);
    }
}
