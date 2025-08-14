package com.smileShark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 定义一个STOMP端点 "/ws" 通过路径：ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置应用程序的前缀为 "/app"，所有以 /app 开头的STOMP目的都被视为应用程序消息
        registry.setApplicationDestinationPrefixes("/app"); // 前端向这个地址发送
        // 消息代理前缀，客户端订阅 /topic 开头的目的地来接收消息
        registry.enableSimpleBroker("/topic"); // 前端从这里获取消息
    }
}
