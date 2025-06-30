package org.example.hugmeexp.domain.bookmark.controller;

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

@RestController
@RequestMapping("/api/v1/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /** 북마크 전체 조회 */
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

    /** 북마크 추가 */
    @PostMapping
    public ResponseEntity<Response<Void>> createBookmark(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BookmarkRequest req
    ) {
        bookmarkService.createBookmark(userDetails.getUsername(), req);

        Response<Void> res = Response.<Void>builder()
                .message("북마크를 추가했습니다.")
                .build();
        return ResponseEntity.ok(res);
    }

    /** 북마크 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<Response<Void>> updateBookmark(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody BookmarkRequest req
    ) {
        bookmarkService.updateBookmark(userDetails.getUsername(), id, req);

        Response<Void> res = Response.<Void>builder()
                .message("북마크를 수정했습니다.")
                .build();
        return ResponseEntity.ok(res);
    }

    /** 북마크 삭제 */
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