package org.example.hugmeexp.global.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 요청 URL이 /api/** 경로일 것
                .allowedOrigins("http://localhost:3000") // 요청 출처가 http://localhost:3000일 것
                .allowedMethods("GET", "POST", "PUT", "DELETE") // HTTP 메서드가 GET, POST, PUT, DELETE 중 하나일 것
                .allowedHeaders("Authorization") // 요청에 Authorization 헤더를 포함할 수 있음 (꼭 포함해야 한다는 뜻은 아님)
                .allowCredentials(true); // 인증 정보(쿠키 또는 헤더)를 포함하는 요청을 허용
    }
}