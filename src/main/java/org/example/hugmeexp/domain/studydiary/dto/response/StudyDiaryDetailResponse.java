package org.example.hugmeexp.domain.studydiary.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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
