package org.example.hugmeexp.domain.studydiary.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class StudyDiaryUpdateRequest {

    @NotNull(message = "제목은 공백일 수 없습니다.")
    @Length(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotNull(message = "내용은 공백일 수 없습니다.")
    @Length(max = 5000, message = "내용은 5000자를 초과할 수 없습니다.")
    private String content;
} 