package org.example.hugmeexp.domain.mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.dto.response.MissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.dto.response.UserMissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.service.MissionTaskService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Missions", description = "미션 CRUD 및 도전,태스크 관리 API")
@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;
    private final MissionTaskService missionTaskService;


    @Operation(
            summary = "모든 미션 조회",
            description = "시스템에 등록된 모든 미션을 조회합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            ))
            }
    )
    @GetMapping
    public ResponseEntity<Response<List<MissionResponse>>> getAllMissions() {
        return ResponseEntity.ok(Response.<List<MissionResponse>>builder()
                .data(missionService.getAllMissions())
                .message("미션 목록을 성공적으로 가져왔습니다.")
                .build());
    }

    @Operation(
            summary = "미션 생성",
            description = "새로운 미션을 생성합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 미션 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MissionRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "생성 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "미션 그룹이 존재하지 않음")
            }
    )
    @PostMapping
    public ResponseEntity<Response<MissionResponse>> createMission(@Valid @RequestBody MissionRequest missionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.<MissionResponse>builder()
                        .data(missionService.createMission(missionRequest))
                        .message("미션이 성공적으로 생성되었습니다.")
                        .build());
    }

    @Operation(
            summary = "미션 조회",
            description = "주어진 ID의 미션 정보를 조회합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Response<MissionResponse>> getMissionById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.<MissionResponse>builder()
                .data(missionService.getMissionById(id))
                .message("미션 " + id + "를 가져왔습니다.")
                .build());
    }

    @Operation(
            summary = "미션 업데이트",
            description = "주어진 ID의 미션을 업데이트합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업데이트할 미션 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MissionRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "업데이트 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Response<MissionResponse>> updateMission(@PathVariable Long id, @Valid @RequestBody MissionRequest missionRequest) {
        return ResponseEntity.ok(Response.<MissionResponse>builder()
                .data(missionService.updateMission(id, missionRequest))
                .message("미션 " + id + "를 업데이트 하였습니다.")
                .build());
    }

    @Operation(
            summary = "미션 삭제",
            description = "주어진 ID의 미션을 삭제합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공 (콘텐츠 없음)"),
                    @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "미션 그룹 변경",
            description = "특정 미션의 그룹을 변경합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42")),
                    @Parameter(name = "missionGroupId",
                            description = "새로운 미션 그룹 ID",
                            in = ParameterIn.QUERY,
                            required = true,
                            schema = @Schema(type = "integer", example = "7"))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "변경 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "미션 또는 그룹을 찾을 수 없음")
            }
    )
    @PatchMapping("/{id}/group")
    public ResponseEntity<Response<MissionResponse>> changeMissionGroup(@PathVariable Long id, @RequestParam Long missionGroupId) {
        return ResponseEntity.ok(Response.<MissionResponse>builder()
                .data(missionService.changeMissionGroup(id, missionGroupId))
                .message("미션 " + id + "의 미션 그룹을 변경하였습니다.")
                .build());
    }

    @Operation(
            summary = "미션 챌린지 조회",
            description = "로그인한 사용자의 해당 유저미션 정보를 조회합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "missionId",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "사용자 또는 미션을 찾을 수 없음")
            }
    )
    @GetMapping("/{missionId}/challenges")
    public ResponseEntity<Response<UserMissionResponse>> getChallenge(@PathVariable Long missionId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.<UserMissionResponse>builder()
                .data(missionService.getUserMission(missionId, userDetails.getUsername()))
                .message("미션 " + missionId + " 도전 정보를 가져왔습니다.")
                .build());
    }

    @Operation(
            summary = "미션 챌린지 생성",
            description = "로그인한 사용자가 해당 미션에 도전합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "도전 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "사용자 또는 미션을 찾을 수 없음")
            }
    )
    @PostMapping("/{id}/challenges")
    public ResponseEntity<Response<UserMissionResponse>> challengeMission(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.<UserMissionResponse>builder()
                .data(missionService.challengeMission(userDetails.getUsername(), id))
                .message("미션 " + id + "에 도전하였습니다.")
                .build());
    }

    @Operation(
            summary = "미션 태스크 목록 조회",
            description = "특정 미션에 연결된 모든 태스크를 조회합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "missionId",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            ))
            }
    )
    @GetMapping("/{missionId}/tasks")
    public ResponseEntity<Response<List<MissionTaskResponse>>> getAllMissionTasksByMissionId(@PathVariable Long missionId) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.<List<MissionTaskResponse>>builder().
                data(missionTaskService.findByMissionId(missionId))
                .build());
    }

    @Operation(
            summary = "내 미션 태스크 목록 조회",
            description = "로그인한 사용자가 상태를 변경했던 태스크 목록을 조회합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "missionId",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "사용자, 미션 또는 유저미션을 찾을 수 없음")
            }
    )
    @GetMapping("/{missionId}/my-tasks")
    public ResponseEntity<Response<List<UserMissionTaskResponse>>> getMyMissionTasks(@PathVariable Long missionId, @AuthenticationPrincipal UserDetails userDetails) {
        List<UserMissionTaskResponse> myTasks = missionTaskService.findUserMissionTasksByUsernameAndMissionId(userDetails.getUsername(), missionId);
        return ResponseEntity.ok(Response.<List<UserMissionTaskResponse>>builder()
                .data(myTasks)
                .message("내 미션 태스크 목록을 성공적으로 가져왔습니다.")
                .build());
    }

    @Operation(
            summary = "미션에 태스크 추가",
            description = "특정 미션에 새로운 태스크를 추가합니다.",
            parameters = {
                    @Parameter(name = "missionId",
                            description = "미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "42"))
            },
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "추가할 태스크 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MissionTaskRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "추가 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            ))
            }
    )
    @PostMapping("/{missionId}/tasks")
    public ResponseEntity<Response<Boolean>> addMissionTask(@PathVariable Long missionId, @Valid @RequestBody MissionTaskRequest request) {
        MissionTaskResponse response = missionTaskService.addMissionTask(missionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.<Boolean>builder()
                .data(response != null)
                .message("미션 태스크를 성공적으로 추가했습니다.")
                .build());
    }
}
