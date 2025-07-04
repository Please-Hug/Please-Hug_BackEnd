package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
import org.example.hugmeexp.domain.praise.enums.PraiseType;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PraiseResponseDTO {

    private Long id;    // PK
    private String senderName;    // 칭찬 보낸 사람 이름
    private List<ReceiverResponseDTO> receivers;
    private String content;    // 칭찬 내용
    private PraiseType type;    // 칭찬 타입
    private long commentCount;    // 댓글 개수
    private List<String> commentProfiles;
    private List<EmojiReactionGroupDTO> emojis;    // 이모지별 반응 수
    private LocalDateTime createdAt;    // 작성 시간

    public static PraiseResponseDTO from(Praise praise,
                                         List<PraiseReceiver> receivers,
                                         long commentCount,
                                         List<EmojiReactionGroupDTO> emojis,
                                         List<UserProfileResponse> commentPro){

        List<ReceiverResponseDTO> receiverDTO = receivers.stream()
                .map(ReceiverResponseDTO::from)
                .toList();

        List<String> commentProfiles = commentPro.stream()
                .map(UserProfileResponse::getProfileImage).toList();

        return PraiseResponseDTO.builder()
                .id(praise.getId())
                .senderName(praise.getSender().getName())
                .receivers(receiverDTO)
                .content(praise.getContent())
                .type(praise.getPraiseType())
                .commentCount(commentCount)
                .commentProfiles(commentProfiles)
                .emojis(emojis)
                .createdAt(praise.getCreatedAt())
                .build();
    }
}
