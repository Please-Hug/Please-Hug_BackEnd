package org.example.hugmeexp.domain.missionTask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.missionTask.enums.TaskState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionTaskResponse {
    private Long id;

    private Long userMissionId;

    private Long missionTaskId;

    private TaskState state;
}
