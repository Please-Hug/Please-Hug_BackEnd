package org.example.hugmeexp.domain.missionTask.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionTaskRequest {
    @NotBlank(message = "태스크 이름은 필수입니다")
    private String name;
    @Positive(message = "공수는 양수여야 합니다")
    private int score; // 공수
}
