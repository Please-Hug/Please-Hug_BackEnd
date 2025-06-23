package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {

    private Long id;    // PK
    private String commenterName;    // 댓글 작성자 이름
    private String content;    // 댓글 내용
    private Map<String, Integer> emojiReactions;    // 이모지별 반응 수
    private LocalDateTime createdAt;    // 작성 시간

    public static CommentResponseDTO from(PraiseComment comment, Map<String, Integer> emojiReactions){
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .commenterName(comment.getCommentWriter().getName())
                .content(comment.getContent())
                .emojiReactions(emojiReactions)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
