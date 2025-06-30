package org.example.hugmeexp.domain.studydiary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "배움일기 좋아요 생성 요청")
public class StudyDiaryLikeCreateRequest {
    @Schema(description = "배움일기 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "배움일기 ID는 필수입니다.")
    private Long studyDiaryId;
}
