package org.example.hugmeexp.domain.bookmark.dto;

import lombok.*;
import org.example.hugmeexp.domain.bookmark.entity.Bookmark;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BookmarkResponse {
    private Long   id;
    private String title;
    private String link;

    public static BookmarkResponse fromEntity(Bookmark b) {
        return BookmarkResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .link(b.getLink())
                .build();
    }
}