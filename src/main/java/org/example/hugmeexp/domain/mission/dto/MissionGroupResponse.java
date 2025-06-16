package org.example.hugmeexp.domain.mission.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionGroupResponse {
    public Long id;
    public Long teacherId;
    public String name;
}
