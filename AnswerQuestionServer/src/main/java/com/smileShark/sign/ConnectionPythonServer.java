package com.smileShark.sign;

import cn.hutool.json.JSONUtil;
import com.smileShark.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConnectionPythonServer {
    private final RestTemplate restTemplate;
    private final Constant constant;
    public String getKpIdSign(Object o){
        return restTemplate.postForObject(constant.KPID_SIGN_URL, Map.of("json", JSONUtil.toJsonStr(o)),String.class);
    }
}
