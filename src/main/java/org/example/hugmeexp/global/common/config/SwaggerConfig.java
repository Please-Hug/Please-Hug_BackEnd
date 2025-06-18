package org.example.hugmeexp.global.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        servers = @Server(url = "/", description = "Default Server URL"),   //기본 서버 주소
        info = @Info(
                title = "HugEXP(GoormEXP cloneCoding) 백엔드 API 명세",  //SwaggerUI 상단에 표시되는 제목
                description = "SpringDoc을 이용한 SwaggerAPI 문서입니다.",   //문서 설명
                version = "1.0",
                contact = @Contact( //관련 문의(보통은 담당자 정보라고 함)
                        name = "SpringDoc 공식문서",
                        url = "https://springdoc.org/"
                )
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("JWT", bearerAuth()));
        //Swagger 문서에 보안 스키마 추가(여기선 JWT Bearer)
        //이게 있으면 상단에 Authorize 버튼이 생김
    }

    public SecurityScheme bearerAuth() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) //인증 방식
                .scheme("bearer")   //위에 스키마 지정한거
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)   //헤더에 인증정보 포함
                .name(HttpHeaders.AUTHORIZATION);   //인증 정보를 포함할 헤더이름
    }
}
