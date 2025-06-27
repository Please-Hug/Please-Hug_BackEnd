package org.example.hugmeexp.domain.qeust.controller;

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
public class QuestAdminController {

    private final QuestAdminService questAdminService;

    /**
     * 퀘스트 생성
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Response<?>> createQuest(
            @RequestBody QuestRequest request) {
        QuestResponse response = questAdminService.createQuest(request);
        return ResponseEntity.status(201).body(Response.builder().data(response).message("Quest successfully created.").build());
    }

    /**
     * 퀘스트 삭제
     * @param questId
     * @return
     */
    @DeleteMapping("/{questId}")
    public ResponseEntity<Response<?>> deleteQuest(
            @PathVariable Long questId) {
        questAdminService.deleteQuest(questId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 퀘스트 수정
     * @param questId
     * @param request
     * @return
     */
    @PutMapping("/{questId}")
    public ResponseEntity<Response<?>> modifyQuest(
            @PathVariable Long questId,
            @RequestBody QuestRequest request) {
        QuestResponse response = questAdminService.modifyQuest(questId, request);
        return ResponseEntity.ok().body(Response.builder().data(response).message("Quest successfully modified.").build());
    }

    /**
     * 유저 퀘스트 할당
     * @param username
     * @return
     */
    @PostMapping("/assign/{username}")
    public ResponseEntity<Response<?>> assignQuest(
            @PathVariable String username) {
        List<UserQuestResponse> response = questAdminService.assignQuest(username);
        return ResponseEntity.ok(Response.builder().data(response).message("User Quest successfully assigned").build());
    }

    /**
     * 모든 유저의 퀘스트 진행 상태를 초기화
     * - 매일 오전 12:00 시에 초기화 작업을 진행하기 위한 기능
     * @return
     */
    @PutMapping("/reset")
    public ResponseEntity<Response<?>> resetQuest() {
        questAdminService.resetQuest();
        return ResponseEntity.noContent().build();
    }

    /**
     * 퀘스트 일괄 생성
     * @return
     */
    @PostMapping("/init")
    public ResponseEntity<Response<?>> initQuest() {
        questAdminService.initQuest();
        return ResponseEntity.noContent().build();
    }

    // ===== 테스트용 =====
    @GetMapping("/allQuest")
    public ResponseEntity<Response<?>> findAllQuest() {
        List<QuestResponse> allQuest = questAdminService.findAllQuest();
        return ResponseEntity.ok().body(Response.builder().data(allQuest).message("전체 퀘스트 조회").build());
    }
}
