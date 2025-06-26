package org.example.hugmeexp.domain.studydiary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryUpdateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.CommentCreateRequest;
import org.example.hugmeexp.domain.studydiary.service.StudyDiaryService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studydiaries")
public class StudyDiaryController {

    private final StudyDiaryService studyDiaryService;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 목록 조회")
    @GetMapping
    public ResponseEntity<Response<Object>> getStudyDiaries(
            @PageableDefault(
                size = 10,              // 기본 페이지 크기
                page = 0,               // 기본 페이지 번호 (0부터 시작)
                sort = "createdAt",     // 기본 정렬 필드
                direction = Sort.Direction.DESC  // 기본 정렬 방향
            ) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {

//        log.info("조회 테스트 : {}", userDetails.getUsername());
        Object studyDiaries = studyDiaryService.getStudyDiaries(pageable);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("배움일기 목록을 성공적으로 조회했습니다.")
                .data(studyDiaries)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 검색")
    @GetMapping("/search")
    public ResponseEntity<Response<Object>> searchStudyDiaries(
            @RequestParam String keyword,
            @PageableDefault(
                    size = 10,              // 기본 페이지 크기
                    page = 0,               // 기본 페이지 번호 (0부터 시작)
                    sort = "createdAt",     // 기본 정렬 필드
                    direction = Sort.Direction.DESC  // 기본 정렬 방향
            ) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Object searchResults = studyDiaryService.searchStudyDiaries(keyword, pageable);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("검색을 성공적으로 완료했습니다.")
                .data(searchResults)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Response<Object>> getStudyDiary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Object studyDiary = studyDiaryService.getStudyDiary(id);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("배움일기를 성공적으로 조회했습니다.")
                .data(studyDiary)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "사용자 배움일기 목록 조회")
    @GetMapping("/user/{user-id}")
    public ResponseEntity<Response<Object>> getUserStudyDiaries(
            @PathVariable("user-id") Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(
                    size = 10,              // 기본 페이지 크기
                    page = 0,               // 기본 페이지 번호 (0부터 시작)
                    sort = "createdAt",     // 기본 정렬 필드
                    direction = Sort.Direction.DESC  // 기본 정렬 방향
            ) Pageable pageable) {
        
        Object userStudyDiaries = studyDiaryService.getUserStudyDiaries(userId, pageable);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("사용자 배움일기 목록을 성공적으로 조회했습니다.")
                .data(userStudyDiaries)
                .build());
    }

//    @Operation(summary = "비슷한 배움일기 추천")
//    @GetMapping("/{id}/similar")
//    public ResponseEntity<Response<Object>> getSimilarStudyDiaries(@PathVariable Long id) {
//
//        Object similarStudyDiaries = studyDiaryService.getSimilarStudyDiaries(id);
//        return ResponseEntity.ok(Response.<Object>builder()
//                .message("비슷한 배움일기를 성공적으로 조회했습니다.")
//                .data(similarStudyDiaries)
//                .build());
//    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 생성")
    @PostMapping
    public ResponseEntity<Response<Object>> createStudyDiary(
            @Valid @RequestBody StudyDiaryCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long studyDiaryId = studyDiaryService.createStudyDiary(request, userDetails);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("성공적으로 생성되었습니다.")
                .data(studyDiaryId)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Response<Object>> updateStudyDiary(
            @PathVariable Long id,
            @Valid @RequestBody StudyDiaryUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long updatedStudyDiaryId = studyDiaryService.updateStudyDiary(id, request, userDetails);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("성공적으로 수정되었습니다.")
                .data(updatedStudyDiaryId)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Object>> deleteStudyDiary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        studyDiaryService.deleteStudyDiary(id, userDetails);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("성공적으로 삭제되었습니다.")
                .data(null)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 임시 저장")
    @PostMapping("/draft")
    public ResponseEntity<Response<Object>> saveDraft(
            @Valid @RequestBody StudyDiaryCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long draftId = studyDiaryService.saveDraft(request, userDetails);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("성공적으로 임시 저장되었습니다.")
                .data(draftId)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "이번 주 작성 상태 조회")
    @GetMapping("/user/{user-id}/week-status")
    public ResponseEntity<Response<Object>> getWeekStatus(
            @PathVariable("user-id") Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Object weekStatus = studyDiaryService.getWeekStatus(userId);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("이번 주 작성 상태를 성공적으로 조회했습니다.")
                .data(weekStatus)
                .build());
    }

//    @SecurityRequirement(name = "JWT")
//    @Operation(summary = "배움일기 내보내기")
//    @GetMapping("/user/{user-id}/export")
//    public ResponseEntity<Response<Object>> exportStudyDiaries(
//            @PathVariable("user-id") Long userId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        Object exportData = studyDiaryService.exportStudyDiaries(userId);
//        return ResponseEntity.ok(Response.<Object>builder()
//                .message("배움일기를 성공적으로 내보냈습니다.")
//                .data(exportData)
//                .build());
//    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "댓글 등록")
    @PostMapping("/{id}/comments")
    public ResponseEntity<Response<Object>> createComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long commentId = studyDiaryService.createComment(id, request, userDetails);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("댓글이 성공적으로 등록되었습니다.")
                .data(commentId)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Response<Object>> deleteComment(
            @PathVariable Long id,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        studyDiaryService.deleteComment(id, commentId, userDetails);
        return ResponseEntity.ok(Response.<Object>builder()
                .message("댓글이 성공적으로 삭제되었습니다.")
                .data(null)
                .build());
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 좋아요 토글", description = "좋아요가 없으면 추가, 있으면 삭제합니다.")
    @PostMapping("/{id}/like")
    public ResponseEntity<Response<Object>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Object totalLike = Integer.valueOf(studyDiaryService.toggleLike(id, userDetails));
        return ResponseEntity.ok(Response.<Object>builder()
                .message("좋아요 상태가 성공적으로 변경되었습니다.")
                .data(totalLike)
                .build());
    }
}
