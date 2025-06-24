package org.example.hugmeexp.domain.praise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.hugmeexp.domain.praise.enums.PraiseType;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PraiseRequestDTO {

    @NotEmpty(message = "칭찬 받는 사람 이름은 필수입니다.")
    private List<String> receiverUsername;    // 칭찬 받은 사람 이름
    @NotBlank(message = "칭찬 내용은 필수입니다.")
    private String content;    // 칭찬 내용
    @NotNull(message = "칭찬 타입은 필수입니다.")
    private PraiseType type;    // 칭찬 타입

}
