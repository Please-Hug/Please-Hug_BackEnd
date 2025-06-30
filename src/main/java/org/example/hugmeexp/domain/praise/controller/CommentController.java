package org.example.hugmeexp.domain.praise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.CommentRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentResponseDTO;
import org.example.hugmeexp.domain.praise.service.CommentService;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/praises")
@RequiredArgsConstructor
@Tag(name = "PraiseComment" , description = "칭찬댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    /* 댓글 작성 */
    @Operation(summary = "칭찬댓글 생성", description = "칭찬 게시물에 새로운 댓글을 생성합니다")
    @PostMapping("/{praiseId}/comments")
    public ResponseEntity<Response<CommentResponseDTO>> createComment(
            @PathVariable Long praiseId,
            @RequestBody CommentRequestDTO commentRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        User commentWriter = userDetails.getUser();
        CommentResponseDTO result = commentService.createComment(praiseId, commentRequestDTO, commentWriter);

        Response<CommentResponseDTO> response = Response.<CommentResponseDTO>builder()
                .message("댓글 작성 완료")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* 댓글 삭제 */
    @Operation(summary = "칭찬댓글 삭제", description = "칭찬 게시물에 새로운 댓글을 삭제합니다")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Response<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        commentService.deleteComment(commentId, userDetails.getUser());

        Response<Void> response = Response.<Void>builder()
                .message("댓글 삭제 완료")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

}
