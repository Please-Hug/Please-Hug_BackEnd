package org.example.hugmeexp.domain.missionTask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionTaskResponse {
    private Long id;
    private Long mission_id;
    private String name;
    private int score; // 공수
    private String tip;
}
