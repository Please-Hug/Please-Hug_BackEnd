package org.example.hugmeexp.domain.mission.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionGroupUpdateRequest {
    public Long id;
    public Long teacherId;
    public String name;
}
