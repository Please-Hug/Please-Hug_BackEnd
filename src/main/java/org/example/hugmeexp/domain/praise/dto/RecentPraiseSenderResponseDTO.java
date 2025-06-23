package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.user.entity.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentPraiseSenderResponseDTO {

    private String name;

    public static RecentPraiseSenderResponseDTO from(User user){
        return RecentPraiseSenderResponseDTO.builder()
                .name(user.getName())
                .build();

    }
}
