package org.example.hugmeexp.domain.missionTask.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.enums.TaskState;
import org.example.hugmeexp.domain.missionTask.service.MissionTaskService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MissionTasks", description = "미션 태스크 CRUD 및 상태 변경 API")
@RestController
@RequestMapping("/api/v1/mission-tasks")
@RequiredArgsConstructor
public class MissionTaskController {
    private final MissionTaskService missionTaskService;

    @Operation(
            summary = "미션 태스크 삭제",
            description = "주어진 미션 태스크를 삭제합니다.",
            parameters = @Parameter(
                    name = "missionTaskId",
                    description = "삭제할 미션 태스크 ID",
                    in = ParameterIn.PATH,
                    required = true,
                    schema = @Schema(type = "integer", example = "100")
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공 (콘텐츠 없음)"),
                    @ApiResponse(responseCode = "404", description = "미션 태스크를 찾을 수 없음")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{missionTaskId}")
    public ResponseEntity<Void> deleteMissionTask(@PathVariable Long missionTaskId) {
        missionTaskService.deleteMissionTask(missionTaskId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "미션 태스크 수정",
            description = "주어진 미션 태스크의 상세 정보를 업데이트합니다.",
            parameters = @Parameter(
                    name = "missionTaskId",
                    description = "수정할 미션 태스크 ID",
                    in = ParameterIn.PATH,
                    required = true,
                    schema = @Schema(type = "integer", example = "100")
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업데이트할 미션 태스크 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MissionTaskRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "미션 태스크를 찾을 수 없음")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{missionTaskId}")
    public ResponseEntity<Response<Boolean>> updateMissionTask(@PathVariable Long missionTaskId, @Valid @RequestBody MissionTaskRequest request) {
        missionTaskService.updateMissionTask(missionTaskId, request);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<Boolean>builder()
                .data(true)
                .message("미션 태스크를 성공적으로 업데이트했습니다.")
                .build());
    }

    @Operation(
            summary = "미션 태스크 상태 업데이트",
            description = "사용자의 미션 태스크 상태를 변경합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "missionTaskId",
                            description = "미션 태스크 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "100")
                    ),
                    @Parameter(
                            name = "taskState",
                            description = "변경할 태스크 상태 (TaskState enum)",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(implementation = TaskState.class)
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "상태 업데이트 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음"),
                    @ApiResponse(responseCode = "404", description = "유저미션을 찾을 수 없음"),
                    @ApiResponse(responseCode = "404", description = "미션 태스크를 찾을 수 없음"),

            }
    )
    @PostMapping("/{missionTaskId}/{taskState}")
    public ResponseEntity<Response<Boolean>> updateMissionTaskState(@PathVariable Long missionTaskId,
                                                                    @PathVariable TaskState taskState,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        missionTaskService.changeUserMissionTaskState(userDetails.getUsername(), missionTaskId, taskState);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<Boolean>builder()
                .data(true)
                .message("미션 태스크 상태를 성공적으로 업데이트했습니다.")
                .build());
    }
}
