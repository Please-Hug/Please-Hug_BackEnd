package org.example.hugmeexp.domain.studydiary.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class StudyDiaryCreateRequest {

    @NotNull(message = "공백일 수 없습니다.")
    private String title;

    @NotNull(message = "공백일 수 없습니다.")
    private String content;
}
