package org.example.hugmeexp.domain.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.hugmeexp.domain.bookmark.entity.Bookmark;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BookmarkResponse {

    @Schema(description = "북마크 고유 id")
    private Long   id;

    @Schema(description = "북마크 이름")
    private String title;

    @Schema(description = "북마크 링크(URL)")
    private String link;



    public static BookmarkResponse fromEntity(Bookmark b) {
        if (b == null) throw new IllegalArgumentException("Bookmark 엔티티는 null일 수 없습니다");
        return BookmarkResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .link(b.getLink())
                .build();
    }
}