package org.example.hugmeexp.domain.studydiary.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyDiaryCommentCreateRequest {

    @NotNull(message = "공백일 수 없습니다.")
    private Long studyDiaryId;

    @NotNull(message = "공백일 수 없습니다.")
    private String content;
}
