package com.smileShark.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateUtil {
    private final RestTemplate restTemplate;

    /**
     * 根据MediaType来铸造不同的请求头，参数也直接存储在HttpEntity中，请求使用exchange发送，
     *
     * @param url           请求路径
     * @param method        请求方式
     * @param contentType   请求头类型
     * @param param         请求参数
     * @param responseType  响应类型
     * @param authorization 授权头
     * @param uriVariables  路径参数
     * @return 响应结果
     */
    public <T> T get(String url, HttpMethod method, MediaType contentType, Object param,
                     Class<T> responseType, String authorization, Map<String, ?> uriVariables) {
        url = builderBrl(url, uriVariables);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<?> request = null;
        // 首先判断MediaType的类型，如果为空，则使用默认的MediaType.APPLICATION_JSON
        if (contentType == null) {
            contentType = MediaType.APPLICATION_JSON;
        }
        headers.setContentType(contentType);
        headers.set("Authorization", authorization);
        // 如果MediaType是表单类型，就使用反射封装MultiValueMap
        if (contentType.includes(MediaType.APPLICATION_FORM_URLENCODED)) {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (param != null) {
                Field[] declaredFields = param.getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    try {
                        params.add(field.getName(), Convert.toStr(field.get(param)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            request = new HttpEntity<>(params, headers);
            // T0D0
        }
        if (contentType.includes(MediaType.APPLICATION_JSON)) {
            request = new HttpEntity<>(param,headers);
        }
        ResponseEntity<T> exchange;
        // 如果有路径参数，则使用exchange带路径参数发送请求
        try {
            exchange = restTemplate.exchange(url, method, request, responseType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(url + " " + exchange.getStatusCode());
        return exchange.getBody();
    }

    public <T> T get(String url, HttpMethod method, MediaType contentType, Object param,
                     ParameterizedTypeReference<T> responseType, String authorization, Map<String, ?> uriVariables) {
        url = builderBrl(url, uriVariables);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<?> request = null;
        // 首先判断MediaType的类型，如果为空，则使用默认的MediaType.APPLICATION_JSON
        if (contentType == null) {
            contentType = MediaType.APPLICATION_JSON;
        }
        headers.setContentType(contentType);
        headers.set("Authorization", authorization);
        // 如果MediaType是表单类型，就使用反射封装MultiValueMap
        if (contentType.includes(MediaType.APPLICATION_FORM_URLENCODED)) {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (param != null) {
                Field[] declaredFields = param.getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    try {
                        params.add(field.getName(), Convert.toStr(field.get(param)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            request = new HttpEntity<>(params, headers);
            // T0D0
        }
        if (contentType.includes(MediaType.APPLICATION_JSON)) {
            request = new HttpEntity<>(param,headers);
        }
        ResponseEntity<T> exchange;
        // 如果有路径参数，则使用exchange带路径参数发送请求
        try {
            exchange = restTemplate.exchange(url, method, request, responseType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(url + " " + exchange.getStatusCode());
        return exchange.getBody();
    }

    public String builderBrl(String url, Map<String, ?> uriVariables) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (uriVariables != null) {
            for (Map.Entry<String, ?> entry : uriVariables.entrySet()) {
                params.add(entry.getKey(), Convert.toStr(entry.getValue()));
            }
        } else {
            return url;
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParams(params);
        return uriBuilder.build().toUriString();
    }
}
