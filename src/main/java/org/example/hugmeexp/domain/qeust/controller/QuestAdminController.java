package org.example.hugmeexp.domain.qeust.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.qeust.dto.QuestRequest;
import org.example.hugmeexp.domain.qeust.dto.QuestResponse;
import org.example.hugmeexp.domain.qeust.dto.UserQuestResponse;
import org.example.hugmeexp.domain.qeust.service.QuestAdminService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/quest")
@Slf4j
@Tag(name = "Admin - Quest", description = "퀘스트 관련 관리자 API")
public class QuestAdminController {

    private final QuestAdminService questAdminService;

    @PostMapping
    @Operation(summary = "퀘스트 생성", description = "일일 퀘스트를 생성한다.")
    public ResponseEntity<Response<?>> createQuest(@RequestBody QuestRequest request) {
        QuestResponse response = questAdminService.createQuest(request);
        return ResponseEntity.status(201).body(Response.builder().data(response).message("Quest successfully created.").build());
    }

    @DeleteMapping("/{questId}")
    @Operation(summary = "퀘스트 삭제", description = "일일 퀘스트를 삭제한다.")
    public ResponseEntity<Response<?>> deleteQuest(@PathVariable Long questId) {
        questAdminService.deleteQuest(questId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{questId}")
    @Operation(summary = "퀘스트 수정", description = "일일 퀘스트를 수정한다.")
    public ResponseEntity<Response<?>> modifyQuest(@PathVariable Long questId, @RequestBody QuestRequest request) {
        QuestResponse response = questAdminService.modifyQuest(questId, request);
        return ResponseEntity.ok().body(Response.builder().data(response).message("Quest successfully modified.").build());
    }

    @PostMapping("/assign/{username}")
    @Operation(summary = "유저 퀘스트 할당", description = "사용자에게 생성된 일일 퀘스트를 할당한다.")
    public ResponseEntity<Response<?>> assignQuest(@PathVariable String username) {
        List<UserQuestResponse> response = questAdminService.assignQuest(username);
        return ResponseEntity.ok(Response.builder().data(response).message("User Quest successfully assigned").build());
    }

    @PutMapping("/reset")
    @Operation(summary = "유저 퀘스트 초기화", description = "모든 사용자의 일일 퀘스트의 진행도를 초기화한다.")
    public ResponseEntity<Response<?>> resetQuest() {
        questAdminService.resetQuest();
        return ResponseEntity.noContent().build();
    }


    // ===== 테스트용 or 사용하지 않는 메서드 =====

    @PostMapping("/init")
    @Operation(summary = "퀘스트 일괄 생성", description = "init.sql로 인해 필요 없어진 호출")
    public ResponseEntity<Response<?>> initQuest() {
        questAdminService.initQuest();
        return ResponseEntity.noContent().build();
    }
}
