package com.smileShark.utils;

import cn.hutool.core.util.IdUtil;

public class VerifyCode {
    public static String createVerifyCode() {
        // 返沪前4位
        return IdUtil.simpleUUID().substring(0, 4);
    }
}
