package org.example.hugmeexp.domain.studydiary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "배움일기 생성 요청")
public class StudyDiaryCreateRequest {

    @Schema(description = "배움일기 제목", example = "Spring Boot 학습 일기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "공백일 수 없습니다.")
    private String title;

    @Schema(description = "배움일기 내용 (Markdown 형식 지원)", 
            example = "# Spring Boot 학습 정리\n\n## 오늘 배운 내용\n\n- **의존성 주입(DI)**: Spring의 핵심 개념\n- **IoC 컨테이너**: 객체의 생명주기 관리\n\n```java\n@Autowired\nprivate UserService userService;\n```\n\n![학습 이미지](https://example.com/study-image.jpg)\n\n> 오늘의 한줄 정리: Spring Boot는 개발 생산성을 크게 향상시켜준다!", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "공백일 수 없습니다.")
    private String content;
}
