package org.example.hugmeexp.domain.studydiary.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyDiaryLikeCreateRequest {
    @NotNull(message = "배움일기 ID는 필수입니다.")
    private Long studyDiaryId;
}
