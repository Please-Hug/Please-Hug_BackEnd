package org.example.hugmeexp.domain.qeust.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.qeust.dto.UserQuestResponse;
import org.example.hugmeexp.domain.qeust.service.QuestService;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quest")
@Slf4j
@Tag(name = "User - Quest", description = "퀘스트 관련 사용자 API")
public class QuestController {

    private final QuestService questService;

    @GetMapping
    @Operation(summary = "사용자 퀘스트 전체 조회", description = "로그인한 유저에게 할당된 일일 퀘스트를 조회한다.")
    public ResponseEntity<Response<?>> getAllQuests(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<UserQuestResponse> response = questService.getAllQuests(user);
        return ResponseEntity.ok().body(Response.builder().data(response).message("All Quests successfully retrieved.").build());
    }

    @PutMapping("/complete/{userQuestId}")
    @Operation(summary = "퀘스트 완료 처리", description = "일일 퀘스트를 완료한다.")
    public ResponseEntity<Response<?>> completeQuest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userQuestId) {
        User user = userDetails.getUser();
        UserQuestResponse response = questService.completeQuest(user, userQuestId);
        return ResponseEntity.ok().body(Response.builder().data(response).message("Quest successfully completed.").build());
    }
}
