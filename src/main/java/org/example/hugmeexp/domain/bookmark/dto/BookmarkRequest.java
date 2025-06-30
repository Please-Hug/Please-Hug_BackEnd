package org.example.hugmeexp.domain.bookmark.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BookmarkRequest {
    private String title;
    private String link;
}