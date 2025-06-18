package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.enums.PraiseType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PraiseResponseDTO {

    private Long id;    // PK
    private String senderName;    // 칭찬 보낸 사람 이름
    private List<String> receiverName;    // 칭찬 받은 사람 이름
    private String content;    // 칭찬 내용
    private PraiseType type;    // 칭찬 타입
    private int commentCount;    // 댓글 개수
    private Map<String, Integer> emojiReactionCount;    // 이모지별 반응 수
    private LocalDateTime createdAt;    // 작성 시간

    public static PraiseResponseDTO from(Praise praise, int commentCount, Map<String, Integer> emojiReactionCount){
        return PraiseResponseDTO.builder()
                .id(praise.getId())
                .senderName(praise.getSenderId().getName())
                .receiverName(List.of(praise.getReceiverId().getName()))
                .content(praise.getContent())
                .type(praise.getPraiseType())
                .commentCount(commentCount)
                .emojiReactionCount(emojiReactionCount)
                .createdAt(praise.getCreatedAt())
                .build();
    }
}
