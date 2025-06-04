package com.smileShark.controller;

import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.entity.User;
import com.smileShark.exception.BusinessException;
import com.smileShark.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 传递有效的token自动登录
    @GetMapping("/login-in")
    @SecurityRequirement(name = "bearerAuth")
    public Result loginIn() {
        return Result.success();
    }

    // 登陆接口
    @PostMapping("/login")
    public Result login(@RequestBody Request request) {
        return userService.login(request);
    }

    // 创建一个验证码给对饮的账号
    @GetMapping("/create-code")
    public Result createCode() {
        return userService.createCode();
    }

    // 验证验证码
    @GetMapping("/verify-code")
    public Result verifyCode() {
        return userService.verifyCode();
    }

    // 获取用户信息
    @GetMapping("/info")
    @SecurityRequirement(name = "bearerAuth")
    public Result info() {
        return userService.getUserInfo();
    }

    @GetMapping("/active-count")
    @SecurityRequirement(name = "bearerAuth")
    public Result activeCount() {
        userService.verifyAdmin();
        return Result.success("查询成功", userService.lambdaQuery().isNotNull(User::getToken).count());
    }

    @GetMapping("/count")
    @SecurityRequirement(name = "bearerAuth")
    public Result count() {
        userService.verifyAdmin();
        return Result.success("查询成功", userService.lambdaQuery().count());
    }

    @GetMapping("/page-list")
    @SecurityRequirement(name = "bearerAuth")
    public Result pageList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) Integer isTest,
            @RequestParam(required = false) Integer identity

    ) {
        userService.verifyAdmin();
        return userService.pageList(page, size, account, userName, isTest, identity);
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    public Result delete(@RequestParam String userId) {
        userService.verifyAdmin();
        User user = new User();
        user.setUserId(userId);
        return Result.success("删除成功", userService.removeById(user));
    }
}
