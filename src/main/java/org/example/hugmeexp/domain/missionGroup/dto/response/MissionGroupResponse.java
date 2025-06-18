package org.example.hugmeexp.domain.missionGroup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionGroupResponse {
    private Long id;
    private String teacherUsername;
    private String name;
}
