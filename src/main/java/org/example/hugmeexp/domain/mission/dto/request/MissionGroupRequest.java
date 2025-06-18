package org.example.hugmeexp.domain.mission.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionGroupRequest {
    private String teacherUsername;
    private String name;
}
