package org.example.hugmeexp.domain.mission.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionGroupUpdateRequest {
    private Long id;
    private Long teacherId;
    private String name;
}
