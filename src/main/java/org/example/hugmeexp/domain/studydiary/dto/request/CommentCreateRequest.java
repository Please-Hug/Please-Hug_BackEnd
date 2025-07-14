package org.example.hugmeexp.domain.studydiary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 생성 요청")
public class CommentCreateRequest {
    @Schema(description = "댓글 내용", example = "좋은 글이네요!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "공백일 수 없습니다.")
    private String content;
}
