package org.example.hugmeexp.domain.bookmark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.bookmark.dto.BookmarkRequest;
import org.example.hugmeexp.domain.bookmark.dto.BookmarkResponse;
import org.example.hugmeexp.domain.bookmark.service.BookmarkService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bookmark", description = "북마크 관련 API")
@RestController
@RequestMapping("/api/v1/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크 전체 조회", description = "로그인한 사용자의 북마크 전체 목록 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<Response<List<BookmarkResponse>>> getBookmarks(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<BookmarkResponse> data = bookmarkService.getBookmarks(username);
        Response<List<BookmarkResponse>> res = Response.<List<BookmarkResponse>>builder()
                .data(data)
                .message("Bookmark status retrieved successfully")
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "북마크 추가", description = "북마크 제목과 링크 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Response<Void>> createBookmark(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid BookmarkRequest req
    ) {
        bookmarkService.createBookmark(userDetails.getUsername(), req);

        Response<Void> res = Response.<Void>builder()
                .message("북마크를 추가했습니다.")
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "북마크 수정", description = "북마크 정보(이름, URL) 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "북마크 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response<Void>> updateBookmark(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody @Valid BookmarkRequest req
    ) {
        bookmarkService.updateBookmark(userDetails.getUsername(), id, req);

        Response<Void> res = Response.<Void>builder()
                .message("북마크를 수정했습니다.")
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "북마크 삭제", description = "특정 북마크 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "북마크 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteBookmark(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        bookmarkService.deleteBookmark(userDetails.getUsername(), id);

        Response<Void> res = Response.<Void>builder()
                .message("북마크를 삭제했습니다.")
                .build();
        return ResponseEntity.ok(res);
    }
}