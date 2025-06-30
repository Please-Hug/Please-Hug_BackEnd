package org.example.hugmeexp.domain.praise.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionResponseDTO;
import org.example.hugmeexp.domain.praise.service.CommentEmojiReactionService;
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
public class CommentEmojiReactionController {

    private final CommentEmojiReactionService commentEmojiReactionService;

    /* 댓글에 반응 생성 */
    @Operation(summary = "칭찬 게시물의 댓글에 반응 생성", description = "칭찬 게시물의 댓글에 반응을 생성합니다.")
    @PostMapping("/{praiseId}/comments/{commentId}/emojis")
    public ResponseEntity<Response<CommentEmojiReactionResponseDTO>> createReaction(
            @PathVariable Long praiseId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentEmojiReactionRequestDTO commentEmojiReactionRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        log.info("반응 등록 요청 : {} ", commentEmojiReactionRequestDTO);

        CommentEmojiReactionResponseDTO result = commentEmojiReactionService.createCommentReaction(praiseId,commentId,commentEmojiReactionRequestDTO,userDetails.getUser());

        Response<CommentEmojiReactionResponseDTO> response = Response.<CommentEmojiReactionResponseDTO>builder()
                .message("댓글 반응 등록 완료")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* 댓글 반응 삭제 */
    @Operation(summary = "칭찬 게시물의 댓글에 반응 삭제", description = "칭찬 게시물의 댓글에 반응을 다시 한번 더 눌러 삭제합니다.")
    @DeleteMapping("/{praiseId}/comments/{commentId}/emojis/{emojiChar}")
    public ResponseEntity<Response<Void>> deleteReaction(
            @PathVariable Long praiseId,
            @PathVariable Long commentId,
            @PathVariable String emojiChar,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        log.info("반응 삭제 요청 : {}", emojiChar);

        commentEmojiReactionService.deleteCommentReaction(praiseId,commentId,emojiChar,userDetails.getUser());

        Response<Void> response = Response.<Void>builder()
                .message("반응 삭제 완료")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
