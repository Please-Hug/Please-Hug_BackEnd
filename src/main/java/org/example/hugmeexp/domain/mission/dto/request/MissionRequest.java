package org.example.hugmeexp.domain.mission.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.hugmeexp.domain.mission.enums.Difficulty;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionRequest {
    @NotNull
    private Long missionGroupId;
    @NotNull
    @Length(min = 1, max = 127)
    private String name;
    @NotNull
    @Length(min = 1, max = 511)
    private String description;
    @NotNull
    private Difficulty difficulty;
    @NotNull
    private int rewardPoint;
    @NotNull
    private int rewardExp;
    @NotNull
    private int order;
    @NotNull
    private int line;
    @Length(max = 511, message = "미션 팁은 최대 511자까지 입력할 수 있습니다.")
    private String tip;
}
