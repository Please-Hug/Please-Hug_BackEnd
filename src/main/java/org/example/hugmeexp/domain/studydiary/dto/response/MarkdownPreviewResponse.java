package org.example.hugmeexp.domain.studydiary.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "마크다운 미리보기 응답")
public class MarkdownPreviewResponse {

    @Schema(description = "원본 마크다운 내용")
    private String markdownContent;
    
    @Schema(description = "HTML로 변환된 내용")
    private String htmlContent;
    
    @Schema(description = "마크다운 내용의 글자 수")
    private int characterCount;
    
    @Schema(description = "마크다운 내용의 단어 수 (공백 기준)")
    private int wordCount;
} 