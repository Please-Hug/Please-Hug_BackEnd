package org.example.hugmeexp.global.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
@EnableMethodSecurity()
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청 URL 허용
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") // 요청 출처가 3000, 5173 포트 일 것
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // HTTP 허용 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 인증 정보(쿠키 또는 헤더)를 포함하는 요청을 허용
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name").toLowerCase();
        String userDir = System.getProperty("user.dir");

        String prefix;
        if (os.contains("win")) prefix = "file:///" + userDir.replace("\\", "/") + "/";
        else prefix = "file:" + userDir + "/";

        registry.addResourceHandler("/profile-images/**")
                .addResourceLocations(prefix + "profile-images/");

        registry.addResourceHandler("/product-images/**")
                .addResourceLocations(prefix + "product-images/");

        registry.addResourceHandler("/mission-uploads/**")
                .addResourceLocations(prefix + "mission-uploads/");
    }
}