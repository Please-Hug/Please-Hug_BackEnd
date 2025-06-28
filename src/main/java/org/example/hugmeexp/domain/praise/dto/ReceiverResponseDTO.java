package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
import org.example.hugmeexp.domain.user.entity.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiverResponseDTO {

    private String name;
    private String profileImage;

    public static ReceiverResponseDTO from(PraiseReceiver receiver){

        User user = receiver.getReceiver();

        return ReceiverResponseDTO.builder()
                .name(user.getName())
                .profileImage(user.getStoredProfileImagePath())
                .build();
    }
}
