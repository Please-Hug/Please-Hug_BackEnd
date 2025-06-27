package org.example.hugmeexp.domain.studydiary.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentCreateRequest {
    @NotNull(message = "공백일 수 없습니다.")
    private String content;
}
