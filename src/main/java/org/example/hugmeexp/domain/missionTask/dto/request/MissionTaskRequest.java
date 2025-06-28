package org.example.hugmeexp.domain.missionTask.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionTaskRequest {
    private String name;
    private int score; // 공수
}
