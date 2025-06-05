package com.smileShark.config;

import com.smileShark.constant.Constant;
import com.smileShark.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    private final TokenInterceptor tokenInterceptor;
    private final Constant constant;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保Swagger资源映射正确
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/public/");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/v3/api-docs")
                .excludePathPatterns(
                        "/static/**",
                        "/public/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/fonts/**",
                        "/favicon.ico",
                        "/index.html",

                        "/",
                        constant.SHIRO_WS_SERVER_URL,
                        "/swagger-ui.html",
                        "/v3/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/user/login" // 登录接口不需要验证token
                );
    }
}