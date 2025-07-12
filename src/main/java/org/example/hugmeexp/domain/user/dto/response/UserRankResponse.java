package org.example.hugmeexp.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.user.entity.User;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class UserRankResponse {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private String name;
    private String description;
    private int level;
    private int exp;
    private int rank;
    private List<MissionGroupResponse> missionGroups;

    public static UserRankResponse from(User user, int rank, int level) {
        return UserRankResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .profileImageUrl(user.getPublicProfileImageUrl())
                .name(user.getName())
                .description(user.getDescription())
                .level(level)
                .exp(user.getExp())
                .rank(rank)
                .missionGroups(user.getUserMissionGroupList().stream().map(userMissionGroup -> MissionGroupResponse.builder()
                        .name(userMissionGroup.getMissionGroup().getName())
                        .build())
                .toList())
                .build();
    }
}
