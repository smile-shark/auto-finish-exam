package com.smileShark.interceptor;

import cn.hutool.core.exceptions.ValidateException;
import com.smileShark.entity.User;
import com.smileShark.exception.BusinessException;
import com.smileShark.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();
    private final TokenUtil tokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        // 输出请求路径
        log.info("请求路径：{}", request.getRequestURI());

        // 1. 获取请求头中的token
        String token = request.getHeader("Authorization");
        if(token == null){
            // 验证失败返回401
            throw new BusinessException("token为空", 401);
        }
        if(token.startsWith("Bearer ")){
            token = tokenUtil.removeBearer(token);
        }
        User user;
        try {
            // 2. 验证token
            boolean verifyResult = tokenUtil.verifyToken(token);
            if(!verifyResult){
                // 验证失败返回401
                throw new BusinessException("token验证失败", 401);
            }
            // 3. 解析token获取用户信息
            user = tokenUtil.parseToken(token, User.class);
        }catch (ValidateException e){
            throw new BusinessException("token已过期", 401);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("token解析失败",401);
        }
        userThreadLocal.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        userThreadLocal.remove();
    }
    public static User getUser() {
        return userThreadLocal.get();
    }
    public static void setUser(User user) {
        userThreadLocal.set(user);
    }
}
