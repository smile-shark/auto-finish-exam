package com.smileShark.utils;

import cn.hutool.core.collection.ListUtil;

public class RedisKeyUtil {
    public static String getSimpleKey(String... keys){
        return String.join(":",ListUtil.of(keys));
    }
}
