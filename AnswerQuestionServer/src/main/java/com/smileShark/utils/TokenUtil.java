package com.smileShark.utils;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.smileShark.constant.Constant;
import com.smileShark.entity.school.response.SchoolLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenUtil {
    private final Constant constant;

    // token拼接
    public static String montage(SchoolLoginResponse response) {
        return response.getToken_type() + " " + response.getAccess_token();
    }

    public String removeBearer(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    /**
     * 创建token
     *
     * @param o 实体类
     * @return token
     */
    public <T> String createToken(T o) {
        Class<?> aClass = o.getClass();
        Map<String, Object> map = new HashMap<>();
        for (Field field : aClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        map.put("expire_time", System.currentTimeMillis());
        return JWT.create().addPayloads(map)
                .setIssuedAt(DateUtil.date())// 设置签发时间
                .setNotBefore(DateUtil.date())// 生效时间
                .setExpiresAt(DateUtil.date(System.currentTimeMillis() + constant.TOKEN_EXPIRATION_TIME * 1000))// 设置过期时间
                .setKey(constant.TOKEN_KEY.getBytes())
                .sign();
    }

    /**
     * 解析token
     *
     * @param token
     * @param tClass
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> T parseToken(String token, Class<T> tClass) throws IllegalAccessException, InstantiationException {
        JWT jwt = JWTUtil.parseToken(token);
        T t = tClass.newInstance();
        for (Field field : tClass.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = jwt.getPayload(field.getName());
            if (value instanceof NumberWithFormat) {
                value = ((NumberWithFormat) value).intValue();
            }
            field.set(t, value);
        }
        return t;
    }

    /**
     * 验证token
     *
     * @param token
     * @return true or false
     */
    public boolean verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        JWTValidator.of(token).validateDate(DateUtil.date());
        return JWTUtil.verify(token, constant.TOKEN_KEY.getBytes());
    }
}
