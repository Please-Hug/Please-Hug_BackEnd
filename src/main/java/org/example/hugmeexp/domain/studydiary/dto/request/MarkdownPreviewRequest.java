package org.example.hugmeexp.domain.studydiary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "마크다운 미리보기 요청")
public class MarkdownPreviewRequest {

    @Schema(description = "미리보기할 마크다운 내용", 
            example = "# 제목\n\n**굵은 글씨**와 *기울임 글씨*\n\n- 리스트 항목 1\n- 리스트 항목 2", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "마크다운 내용은 공백일 수 없습니다.")
    private String markdownContent;
} 