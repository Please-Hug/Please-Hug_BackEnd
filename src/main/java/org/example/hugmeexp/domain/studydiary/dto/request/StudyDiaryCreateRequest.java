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

    @Schema(description = "배움일기 내용", example = "오늘은 Spring Boot의 기본 개념에 대해 학습했습니다...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "공백일 수 없습니다.")
    private String content;
}
