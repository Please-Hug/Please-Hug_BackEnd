package org.example.hugmeexp.domain.studydiary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@Schema(description = "배움일기 수정 요청")
public class StudyDiaryUpdateRequest {

    @Schema(description = "수정할 배움일기 제목", example = "Spring Boot 심화 학습 일기", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @NotNull(message = "제목은 공백일 수 없습니다.")
    @Length(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @Schema(description = "수정할 배움일기 내용 (Markdown 형식 지원)", 
            example = "# Spring Boot 심화 학습\n\n## 수정된 내용\n\n- **AOP(Aspect-Oriented Programming)**: 횡단 관심사 분리\n- **Spring Security**: 인증과 권한 부여\n\n```java\n@EnableWebSecurity\npublic class SecurityConfig {\n    // 보안 설정\n}\n```\n\n> 심화 학습을 통해 더 깊이 이해하게 되었다.", 
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 10000)
    @NotNull(message = "내용은 공백일 수 없습니다.")
    @Length(max = 10000, message = "내용은 10000자를 초과할 수 없습니다.")
    private String content;
} 