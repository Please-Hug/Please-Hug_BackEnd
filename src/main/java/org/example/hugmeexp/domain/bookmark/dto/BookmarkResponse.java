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
        if (b == null) throw new IllegalArgumentException("Bookmark 엔티티는 null일 수 없습니다");
        return BookmarkResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .link(b.getLink())
                .build();
    }
}