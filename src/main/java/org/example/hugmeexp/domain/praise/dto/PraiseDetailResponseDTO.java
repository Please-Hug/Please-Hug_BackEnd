package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
import org.example.hugmeexp.domain.praise.enums.PraiseType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PraiseDetailResponseDTO {

    private Long id;    // 칭찬 ID
    private String senderName;    // 칭찬을 보낸 사람
    private List<ReceiverResponseDTO> receivers;
    private String content;    // 칭찬 내용
    private PraiseType type;    // 칭찬 타입
    private LocalDateTime createdAt;    // 칭찬을 작성한 시간
    private List<EmojiReactionGroupDTO> emojiReactions;    // 이모지별 반응 수
    private int commentCount;    // 댓글 수
    private List<CommentResponseDTO> comments;     // 댓글 리스트

    public static PraiseDetailResponseDTO from(Praise praise,
                                               List<PraiseReceiver> receivers,
                                               List<PraiseComment> commentList,
                                               List<EmojiReactionGroupDTO> emojiReactions,
                                               Map<Long, Map<String,Integer>> commentEmojiMap){

        List<ReceiverResponseDTO> receiverDTO = receivers.stream()
                .map(ReceiverResponseDTO::from)
                .toList();

        List<CommentResponseDTO> commentResponse = commentList.stream()
                .map(comment -> {Map<String,Integer> emojis = commentEmojiMap.getOrDefault(comment.getId(),Map.of());
            return CommentResponseDTO.from(comment, emojis);
        }).toList();


        return PraiseDetailResponseDTO.builder()
                .id(praise.getId())
                .senderName(praise.getSender().getName())
                .receivers(receiverDTO)
                .content(praise.getContent())
                .type(praise.getPraiseType())
                .createdAt(praise.getCreatedAt())
                .emojiReactions(emojiReactions)
                .commentCount(commentList.size())
                .comments(commentResponse)
                .build();
    }
}
