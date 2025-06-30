package org.example.hugmeexp.domain.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BookmarkRequest {

    @Schema(description = "북마크 이름")
    private String title;

    @Schema(description = "북마크 링크(URL)")
    private String link;
}