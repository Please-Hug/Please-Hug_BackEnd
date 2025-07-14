package org.example.hugmeexp.domain.bookmark.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BookmarkRequest {

    @NotBlank(message = "북마크 제목은 필수입니다")
    @Schema(description = "북마크 이름")
    private String title;

    @NotBlank(message = "북마크 링크는 필수입니다")
    @Schema(description = "북마크 링크(URL)")
    private String link;
}