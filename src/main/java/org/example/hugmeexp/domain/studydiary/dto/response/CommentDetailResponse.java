package org.example.hugmeexp.domain.studydiary.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryComment;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDetailResponse {

    private Long id;

    private Long userId;

    private String name;

    private String content;

    private LocalDateTime createdAt;

    public static CommentDetailResponse buildToResponse(StudyDiaryComment comment){
        return CommentDetailResponse.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .name(comment.getUser().getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
