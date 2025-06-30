package org.example.hugmeexp.domain.praise.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.PraiseEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseEmojiReactionResponseDTO;
import org.example.hugmeexp.domain.praise.service.PraiseEmojiReactionService;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j    // 로깅 어노테이션
@RestController
@RequestMapping("/api/v1/praises")
@RequiredArgsConstructor
public class PraiseEmojiReactionController {

    private final PraiseEmojiReactionService praiseEmojiReactionService;

    /* 칭찬 게시물에 반응 생성 */
    @Operation(summary = "칭찬게시물의 반응 생성", description = "칭찬게시물의 반응을 생성합니다.")
    @PostMapping("/{praiseId}/emojis")
    public ResponseEntity<Response<PraiseEmojiReactionResponseDTO>> addEmojiReaction(
            @PathVariable Long praiseId,
            @Valid @RequestBody PraiseEmojiReactionRequestDTO praiseEmojiReactionRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        log.info("Praise emoji reaction creation request - praiseId: {}, userId: {}, emoji: {}",
                praiseId, userDetails.getUser().getId(), praiseEmojiReactionRequestDTO.getEmoji());

        PraiseEmojiReactionResponseDTO praiseEmojiReactionResponseDTO = praiseEmojiReactionService.addEmojiReaction(praiseId,userDetails.getUser(),praiseEmojiReactionRequestDTO);

        Response<PraiseEmojiReactionResponseDTO> response = Response.<PraiseEmojiReactionResponseDTO>builder()
                .message("칭찬 게시물에 반응 생성 완료")
                .data(praiseEmojiReactionResponseDTO)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* 칭찬 게시물에 반응 삭제 */
    @Operation(summary = "칭찬게시물의 반응 삭제", description = "칭찬게시물의 반응을 삭제합니다.")
    @DeleteMapping("/{praiseId}/emojis/{emojiId}")
    public ResponseEntity<Response<Void>> deleteEmojiReaction(
            @PathVariable Long praiseId,
            @PathVariable Long emojiId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        log.info("Praise emoji reaction deletion request - praiseId: {}, emojiId: {}, userId: {}",
                praiseId, emojiId, userDetails.getUser().getId());

        praiseEmojiReactionService.deleteEmojiReaction(praiseId,emojiId,userDetails.getUser());

        Response<Void> response = Response.<Void>builder()
                .message("칭찬 게시물 반응 삭제 완료")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

}
