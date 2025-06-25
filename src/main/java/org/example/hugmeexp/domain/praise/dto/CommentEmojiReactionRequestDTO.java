package org.example.hugmeexp.domain.praise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEmojiReactionRequestDTO {

    @NotBlank(message = "이모지는 필수 입니다")
    @Size(max = 10, message = "이모지는 10자를 초과할 수 없습니다")
    private String emoji;    // 이모지
}
