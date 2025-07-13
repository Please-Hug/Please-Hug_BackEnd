package org.example.hugmeexp.domain.missionGroup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.dto.response.UserMissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.service.MissionGroupService;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MissionGroups", description = "미션 그룹 CRUD 및 멤버,미션 관리 API")
@RestController
@RequestMapping("/api/v1/mission-groups")
@RequiredArgsConstructor
public class MissionGroupController {
    private final MissionGroupService missionGroupService;
    private final MissionService missionService;

    @Operation(
            summary = "모든 미션 그룹 조회",
            description = "시스템에 등록된 모든 미션 그룹 목록을 반환합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Response<List<MissionGroupResponse>>> getMissionGroups() {
        List<MissionGroupResponse> missionGroups = missionGroupService.getAllMissionGroups();
        return ResponseEntity.ok().body(Response.<List<MissionGroupResponse>>builder().data(missionGroups).message("미션 그룹 목록을 성공적으로 가져왔습니다.").build());
    }

    @Operation(
            summary = "내 미션 그룹 조회",
            description = "로그인된 사용자가 속한 미션 그룹 목록을 반환합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )
                    )
            }
    )
    @GetMapping("/my")
    public ResponseEntity<Response<List<UserMissionGroupResponse>>> getMyMissionGroups(@AuthenticationPrincipal UserDetails user) {
        List<UserMissionGroupResponse> missionGroups = missionGroupService.getMyMissionGroups(user.getUsername());
        return ResponseEntity.ok().body(Response.<List<UserMissionGroupResponse>>builder().data(missionGroups).message("미션 그룹 목록을 성공적으로 가져왔습니다.").build());
    }

    @Operation(
            summary = "미션 그룹 생성",
            description = "새로운 미션 그룹을 생성합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 미션 그룹 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MissionGroupRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "생성 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "강사 정보를 찾을 수 없음")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Response<MissionGroupResponse>> createMissionGroup(@Valid @RequestBody MissionGroupRequest request, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(201).body(Response.<MissionGroupResponse>builder().data(missionGroupService.createMissionGroup(request, user.getUsername())).message("미션 그룹을 생성하였습니다.").build());
    }

    @Operation(
            summary = "미션 그룹 조회",
            description = "ID에 해당하는 미션 그룹 정보를 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "미션 그룹 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "미션 그룹을 찾을 수 없음")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Response<MissionGroupResponse>> getMissionGroup(@PathVariable Long id) {
        List<MissionGroupResponse> missionGroups = missionGroupService.getMissionGroupById(id);
        if (missionGroups.isEmpty()) {
            throw new MissionGroupNotFoundException();
        }
        return ResponseEntity.ok().body(Response.<MissionGroupResponse>builder().data(missionGroups.get(0)).message("미션그룹 " + id + "를 가져왔습니다.").build());
    }

    @Operation(
            summary = "미션 그룹 수정",
            description = "ID에 해당하는 미션 그룹 정보를 업데이트합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "미션 그룹 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 미션 그룹 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MissionGroupRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "404", description = "미션 그룹을 찾을 수 없음"),
                    @ApiResponse(responseCode = "404", description = "강사 정보를 찾을 수 없음")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Response<MissionGroupResponse>> updateMissionGroup(@PathVariable Long id, @Valid @RequestBody MissionGroupRequest request) {
        return ResponseEntity.ok().body(Response.<MissionGroupResponse>builder().data(missionGroupService.updateMissionGroup(id, request)).message("미션그룹 " + id + "를 업데이트 하였습니다.").build());
    }

    @Operation(
            summary = "미션 그룹 삭제",
            description = "ID에 해당하는 미션 그룹을 삭제합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "미션 그룹 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "미션 그룹을 찾을 수 없음")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMissionGroup(@PathVariable Long id) {
        missionGroupService.deleteMissionGroup(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "미션 그룹별 미션 조회",
            description = "특정 미션 그룹에 속한 미션 목록을 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "미션 그룹 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "미션 그룹을 찾을 수 없음")
            }
    )
    @GetMapping("/{id}/missions")
    public ResponseEntity<Response<List<MissionResponse>>> getMissionsByMissionGroupId(@PathVariable Long id) {
        return ResponseEntity.ok().body(Response.<List<MissionResponse>>builder()
                .data(missionService.getMissionsByMissionGroupId(id))
                .message("미션 그룹 " + id + "의 미션 목록을 가져왔습니다.")
                .build());
    }

    @Operation(
            summary = "미션 그룹 멤버 조회",
            description = "특정 미션 그룹에 속한 사용자 목록을 조회합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "missionGroupId",
                            description = "미션 그룹 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "미션 그룹을 찾을 수 없음")
            }
    )
    @GetMapping("/{missionGroupId}/users")
    public ResponseEntity<Response<List<UserProfileResponse>>> getUsersInMissionGroup(@PathVariable Long missionGroupId) {
        List<UserProfileResponse> users = missionGroupService.getUsersInMissionGroup(missionGroupId);
        return ResponseEntity.ok().body(Response.<List<UserProfileResponse>>builder().data(users).message("미션 그룹 " + missionGroupId + "의 사용자 목록을 가져왔습니다.").build());
    }

    @Operation(
            summary = "미션 그룹에 사용자 추가",
            description = "특정 미션 그룹에 사용자를 추가합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "missionGroupId",
                            description = "미션 그룹 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "5")),
                    @Parameter(name = "userId",
                            description = "추가할 사용자 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "추가 성공"),
                    @ApiResponse(responseCode = "404", description = "미션 그룹 또는 사용자 없음")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{missionGroupId}/users/{username}")
    public ResponseEntity<Response<Void>> addUserToMissionGroup(@PathVariable Long missionGroupId,
                                                                @PathVariable String username) {
        missionGroupService.addUserToMissionGroup(username, missionGroupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.<Void>builder().message("사용자 " + username + "를 미션 그룹 " + missionGroupId + "에 추가하였습니다.").build());
    }

    @Operation(
            summary = "미션 그룹에서 사용자 제거",
            description = "특정 미션 그룹에서 사용자를 제거합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "missionGroupId",
                            description = "미션 그룹 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "5")),
                    @Parameter(name = "userId",
                            description = "제거할 사용자 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "제거 성공"),
                    @ApiResponse(responseCode = "404", description = "미션 그룹 또는 사용자 없음")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{missionGroupId}/users/{username}")
    public ResponseEntity<Response<Void>> removeUserFromMissionGroup(@PathVariable Long missionGroupId,
                                                                     @PathVariable String username) {
        missionGroupService.removeUserFromMissionGroup(username, missionGroupId);
        return ResponseEntity.ok().body(Response.<Void>builder().message("사용자 " + username + "를 미션 그룹 " + missionGroupId + "에서 제거하였습니다.").build());
    }

    @Operation(
            summary = "미션 그룹별 챌린지 목록 조회",
            description = "로그인된 사용자의 특정 미션 그룹 챌린지(유저미션) 목록을 조회합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "missionGroupId",
                            description = "미션 그룹 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "5"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "미션 그룹 또는 사용자 없음")
            }
    )
    @GetMapping("/{missionGroupId}/challenges")
    public ResponseEntity<Response<List<UserMissionResponse>>> getMissionGroupChallenges(@PathVariable Long missionGroupId, @AuthenticationPrincipal UserDetails userDetails) {
        List<UserMissionResponse> challenges = missionGroupService.findUserMissionByUsernameAndMissionGroup(userDetails.getUsername(), missionGroupId);
        return ResponseEntity.ok().body(Response.<List<UserMissionResponse>>builder().data(challenges).message("사용자 " + userDetails.getUsername() + "의 미션 그룹 " + missionGroupId + " 도전 목록을 가져왔습니다.").build());
    }
}
