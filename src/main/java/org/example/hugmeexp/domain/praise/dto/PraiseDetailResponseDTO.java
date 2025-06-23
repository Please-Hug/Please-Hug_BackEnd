package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
import org.example.hugmeexp.domain.praise.enums.PraiseType;
import org.example.hugmeexp.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PraiseDetailResponseDTO {

    private Long id;    // 칭찬 ID
    private String senderName;    // 칭찬을 보낸 사람
    private List<String> receiverName;    // 칭찬을 받은 사람
    private String content;    // 칭찬 내용
    private PraiseType type;    // 칭찬 타입
    private LocalDateTime createdAt;    // 칭찬을 작성한 시간
    private Map<String, Integer> emojiReactions;    // 이모지별 반응 수
    private int commentCount;    // 댓글 수
    private List<CommentResponseDTO> comments;     // 댓글 리스트
//    private String profileImageUrl;

    public static PraiseDetailResponseDTO from(Praise praise, List<PraiseReceiver> receivers, List<PraiseComment> commentList, Map<String, Integer> emojiReactions){

        List<String> receiverNames = receivers.stream()
                .map(praiseReceiver -> praiseReceiver.getReceiver().getName())
                .toList();

        return PraiseDetailResponseDTO.builder()
                .id(praise.getId())
                .senderName(praise.getSender().getName())
                .receiverName(receiverNames)
                .content(praise.getContent())
                .type(praise.getPraiseType())
                .createdAt(praise.getCreatedAt())
                .emojiReactions(emojiReactions)
                .commentCount(commentList.size())
                .comments(commentList.stream().map(CommentResponseDTO::from).collect(Collectors.toList()))
                .build();
    }
}
