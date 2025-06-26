package org.example.hugmeexp.global.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 요청 URL이 /api/** 경로일 것
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") // 요청 출처가 3000, 5173 포트 일 것
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // HTTP 메서드가 GET, POST, PUT, DELETE, PATCH 중 하나일 것
                .allowedHeaders("Authorization", "Content-Type") // Authorization, Content-Type 헤더 허용
                .allowCredentials(true); // 인증 정보(쿠키 또는 헤더)를 포함하는 요청을 허용
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //profile-images 경로로 접근시 /application/profile-images/ 로 서빙
        registry.addResourceHandler("/profile-images/**")
                .addResourceLocations("file:/application/profile-images/");

        //product-images 경로로 접근시 /application/product-images/ 로 서빙
        registry.addResourceHandler("/product-images/**")
                .addResourceLocations("file:/application/product-images/");

        //mission-uploads 경로로 접근시 /application/mission-uploads/ 로 서빙
        registry.addResourceHandler("/mission-uploads/**")
                .addResourceLocations("file:/application/mission-uploads/");
    }
}