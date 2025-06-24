package org.example.hugmeexp.domain.qeust.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.qeust.dto.QuestResponse;
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
public class QuestController {

    private final QuestService questService;

    /**
     * 사용자에게 할당된 모든 퀘스트 조회
     * @param userDetails
     * @return
     */
    @GetMapping
    public ResponseEntity<Response<?>> getAllQuests(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<UserQuestResponse> response = questService.getAllQuests(user);
        return ResponseEntity.ok().body(Response.builder().data(response).message("All Quests successfully retrieved.").build());
    }

    /**
     * 퀘스트 완료
     * @param userDetails
     * @param userQuestId
     * @return
     */
    @PutMapping("/complete/{userQuestId}")
    public ResponseEntity<Response<?>> completeQuest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userQuestId) {
        User user = userDetails.getUser();
        UserQuestResponse response = questService.completeQuest(user, userQuestId);
        return ResponseEntity.ok().body(Response.builder().data(response).message("Quest successfully completed.").build());
    }
}
