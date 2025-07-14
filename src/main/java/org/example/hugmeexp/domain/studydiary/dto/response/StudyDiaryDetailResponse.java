package org.example.hugmeexp.domain.studydiary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyDiaryDetailResponse {

    private Long id;

    private Long userId;

    private String name;

    private String title;

    private String content;

    private int likeNum;

    List<CommentDetailResponse> commentList;

    private LocalDateTime createdAt;
}
