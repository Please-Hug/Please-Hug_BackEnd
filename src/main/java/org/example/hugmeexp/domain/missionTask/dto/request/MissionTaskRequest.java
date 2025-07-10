package org.example.hugmeexp.domain.missionTask.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionTaskRequest {
    @NotBlank(message = "태스크 이름은 필수입니다")
    private String name;
    @Positive(message = "공수는 양수여야 합니다")
    private int score; // 공수
    @Length(max = 511, message = "팁은 최대 511자까지 가능합니다")
    private String tip;
}
