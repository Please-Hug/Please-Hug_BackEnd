package org.example.hugmeexp.domain.studydiary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryCreateRequest;
import org.example.hugmeexp.domain.studydiary.service.StudyDiaryService;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studydiaries")
public class StudyDiaryController {

    private final StudyDiaryService studyDiaryService;

    @Operation(summary = "배움일기 목록 조회")
    @GetMapping
    public Response.ResponseBuilder<Object> getStudyDiaries(
            @RequestParam(required = false) String sort,
            Pageable pageable) {
        
        Object studyDiaries = studyDiaryService.getStudyDiaries(sort, pageable);
        return Response.builder()
                .message("배움일기 목록을 성공적으로 조회했습니다.")
                .data(studyDiaries);
    }

    @Operation(summary = "배움일기 검색")
    @GetMapping("/search")
    public Response.ResponseBuilder<Object> searchStudyDiaries(
            @RequestParam String keyword,
            Pageable pageable) {
        
        Object searchResults = studyDiaryService.searchStudyDiaries(keyword, pageable);
        return Response.builder()
                .message("검색을 성공적으로 완료했습니다.")
                .data(searchResults);
    }

    @Operation(summary = "배움일기 상세 조회")
    @GetMapping("/{id}")
    public Response.ResponseBuilder<Object> getStudyDiary(@PathVariable Long id) {
        
        Object studyDiary = studyDiaryService.getStudyDiary(id);
        return Response.builder()
                .message("배움일기를 성공적으로 조회했습니다.")
                .data(studyDiary);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "사용자 배움일기 목록 조회")
    @GetMapping("/user/{user-id}")
    public Response.ResponseBuilder<Object> getUserStudyDiaries(
            @PathVariable("user-id") Long userId,
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        
        Object userStudyDiaries = studyDiaryService.getUserStudyDiaries(userId, pageable);
        return Response.builder()
                .message("사용자 배움일기 목록을 성공적으로 조회했습니다.")
                .data(userStudyDiaries);
    }

    @Operation(summary = "비슷한 배움일기 추천")
    @GetMapping("/{id}/similar")
    public Response.ResponseBuilder<Object> getSimilarStudyDiaries(@PathVariable Long id) {
        
        Object similarStudyDiaries = studyDiaryService.getSimilarStudyDiaries(id);
        return Response.builder()
                .message("비슷한 배움일기를 성공적으로 조회했습니다.")
                .data(similarStudyDiaries);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 생성")
    @PostMapping
    public Response.ResponseBuilder<Object> createStudyDiary(
            @Valid @RequestBody StudyDiaryCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long studyDiaryId = studyDiaryService.createStudyDiary(request, userDetails);
        return Response.builder()
                .message("성공적으로 생성되었습니다.")
                .data(studyDiaryId);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 임시 저장")
    @PostMapping("/draft")
    public Response.ResponseBuilder<Object> saveDraft(
            @Valid @RequestBody StudyDiaryCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long draftId = studyDiaryService.saveDraft(request, userDetail);
        return Response.builder()
                .message("성공적으로 임시 저장되었습니다.")
                .data(draftId);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "이번 주 작성 상태 조회")
    @GetMapping("/user/{user-id}/week-status")
    public Response.ResponseBuilder<Object> getWeekStatus(
            @PathVariable("user-id") Long userId,
            @AuthenticationPrincipal User user) {
        
        Object weekStatus = studyDiaryService.getWeekStatus(userId);
        return Response.builder()
                .message("이번 주 작성 상태를 성공적으로 조회했습니다.")
                .data(weekStatus);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "배움일기 내보내기")
    @GetMapping("/user/{user-id}/export")
    public Response.ResponseBuilder<Object> exportStudyDiaries(
            @PathVariable("user-id") Long userId,
            @AuthenticationPrincipal User user) {
        
        Object exportData = studyDiaryService.exportStudyDiaries(userId);
        return Response.builder()
                .message("배움일기를 성공적으로 내보냈습니다.")
                .data(exportData);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "댓글 등록")
    @PostMapping("/{id}/comments")
    public Response.ResponseBuilder<Object> createComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal User user) {
        
        Long commentId = studyDiaryService.createComment(id, request, user);
        return Response.builder()
                .message("댓글이 성공적으로 등록되었습니다.")
                .data(commentId);
    }
}
