package com.smileShark;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class GlobalTest {
    @Test
    public void uuidTest(){
        for (int i = 0; i < 10; i++){
            System.out.println(IdUtil.simpleUUID());
        }
    }
}
