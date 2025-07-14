package org.example.hugmeexp.domain.missionGroup.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MissionGroupResponse {
    private Long id;
    private UserProfileResponse teacher;
    private String name;
}
