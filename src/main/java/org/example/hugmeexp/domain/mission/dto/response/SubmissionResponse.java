package org.example.hugmeexp.domain.mission.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SubmissionResponse {
    private Long id;

    private UserMissionResponse userMission;

    private String fileName;

    private String originalFileName;

    private String comment;

    private String feedback;
}
