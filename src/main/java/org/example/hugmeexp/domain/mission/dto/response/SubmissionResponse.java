package org.example.hugmeexp.domain.mission.dto.response;

import lombok.*;
import org.example.hugmeexp.domain.mission.entity.UserMission;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SubmissionResponse {
    private Long id;

    private UserMission userMission;

    private String fileName;

    private String originalFileName;

    private String comment;

    private String feedback;
}
