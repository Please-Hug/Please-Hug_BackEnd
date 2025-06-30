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

    @Schema(description = "수정할 배움일기 내용", example = "오늘은 Spring Boot의 고급 기능들에 대해 더 깊이 학습했습니다...", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 5000)
    @NotNull(message = "내용은 공백일 수 없습니다.")
    @Length(max = 5000, message = "내용은 5000자를 초과할 수 없습니다.")
    private String content;
} 