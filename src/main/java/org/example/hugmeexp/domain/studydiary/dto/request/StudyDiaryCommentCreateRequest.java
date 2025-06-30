package org.example.hugmeexp.domain.studydiary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "배움일기 댓글 생성 요청")
public class StudyDiaryCommentCreateRequest {

    @Schema(description = "배움일기 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "공백일 수 없습니다.")
    private Long studyDiaryId;

    @Schema(description = "댓글 내용", example = "정말 유익한 내용이에요!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "공백일 수 없습니다.")
    private String content;
}
