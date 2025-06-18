package org.example.hugmeexp.domain.mission.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionGroupRequest {
    @NotBlank
    @Length(min = 1, max = 32)
    private String teacherUsername;
    @NotBlank
    @Length(min = 1, max = 127)
    private String name;
}
