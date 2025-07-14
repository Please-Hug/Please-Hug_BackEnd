package org.example.hugmeexp.domain.studydiary.controller;

/**
 * StudyDiary 컨트롤러 테스트
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.studydiary.dto.request.CommentCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryUpdateRequest;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryDetailResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryFindAllResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryMyHomeResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryWeekStatusResponse;
import org.example.hugmeexp.domain.studydiary.service.StudyDiaryService;
import org.example.hugmeexp.domain.studydiary.service.StudyDiaryRedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("StudyDiary 컨트롤러 테스트")
class StudyDiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudyDiaryService studyDiaryService;

    @MockBean
    private StudyDiaryRedisService studyDiaryRedisService;

    @Test
    @DisplayName("GET /api/v1/studydiaries - 배움일기 목록 조회 성공")
    void getStudyDiaries_Success() throws Exception {
        // given
        List<StudyDiaryFindAllResponse> diaryList = Arrays.asList(
                createMockStudyDiaryResponse(1L, "제목1", "testuser"),
                createMockStudyDiaryResponse(2L, "제목2", "testuser2")
        );
        Page<StudyDiaryFindAllResponse> mockPage = new PageImpl<>(diaryList, PageRequest.of(0, 10), 2);
        
        when(studyDiaryService.getStudyDiaries(any(Pageable.class))).thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries")
                        .with(user("testuser"))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("배움일기 목록을 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].title").value("제목1"));

        verify(studyDiaryService, times(1)).getStudyDiaries(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/weekly/popular - 일주일간 인기 배움일기 조회 성공")
    void getWeeklyPopularStudyDiaries_Success() throws Exception {
        // given
        List<StudyDiaryFindAllResponse> popularDiaries = Arrays.asList(
                createMockStudyDiaryResponse(1L, "인기글1", "author1", 100, 10),
                createMockStudyDiaryResponse(2L, "인기글2", "author2", 50, 5)
        );
        Page<StudyDiaryFindAllResponse> mockPage = new PageImpl<>(popularDiaries);
        
        when(studyDiaryRedisService.getCachedWeeklyPopularDiaries(any(Pageable.class))).thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/weekly/popular")
                        .with(user("testuser")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("일주일간 인기 배움일기를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.content[0].likeNum").value(100));

        verify(studyDiaryRedisService, times(1)).getCachedWeeklyPopularDiaries(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/user/{user-id} - 사용자 배움일기 목록 조회 성공")
    void getUserStudyDiaries_Success() throws Exception {
        // given
        Long userId = 1L;
        List<StudyDiaryFindAllResponse> userDiaries = Arrays.asList(
                createMockStudyDiaryResponse(1L, "사용자 글 1", "testuser"),
                createMockStudyDiaryResponse(2L, "사용자 글 2", "testuser")
        );
        
        when(studyDiaryService.getUserStudyDiaries(eq(userId), any(Pageable.class))).thenReturn(userDiaries);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/user/{user-id}", userId)
                        .with(user("testuser")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 배움일기 목록을 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("사용자 글 1"));

        verify(studyDiaryService, times(1)).getUserStudyDiaries(eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/search - 배움일기 검색 성공")
    void searchStudyDiaries_Success() throws Exception {
        // given
        String keyword = "Spring";
        List<StudyDiaryFindAllResponse> searchResults = Arrays.asList(
                createMockStudyDiaryResponse(1L, "Spring Boot 학습", "author1"),
                createMockStudyDiaryResponse(2L, "Spring Security 정리", "author2")
        );
        Page<StudyDiaryFindAllResponse> mockPage = new PageImpl<>(searchResults);
        
        when(studyDiaryService.searchStudyDiaries(eq(keyword), any(Pageable.class))).thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/search")
                        .with(user("testuser"))
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("검색을 성공적으로 완료했습니다."))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(studyDiaryService, times(1)).searchStudyDiaries(eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/{id} - 배움일기 상세 조회 성공")
    void getStudyDiary_Success() throws Exception {
        // given
        Long diaryId = 1L;
        StudyDiaryDetailResponse mockResponse = createMockDetailResponse(diaryId);
        
        when(studyDiaryService.getStudyDiary(diaryId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/{id}", diaryId)
                        .with(user("testuser")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("배움일기를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.id").value(diaryId));

        verify(studyDiaryService, times(1)).getStudyDiary(diaryId);
    }

    @Test
    @DisplayName("POST /api/v1/studydiaries - 배움일기 생성 성공")
    void createStudyDiary_Success() throws Exception {
        // given
        Long createdId = 1L;
        
        StudyDiaryCreateRequest request = new StudyDiaryCreateRequest();
        request.setTitle("Spring Boot 학습 일기");
        request.setContent("# Spring Boot 학습 정리\\n\\n## 오늘 배운 내용");

        when(studyDiaryService.createStudyDiary(any(StudyDiaryCreateRequest.class), any())).thenReturn(createdId);

        // when & then
        mockMvc.perform(post("/api/v1/studydiaries")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 생성되었습니다."))
                .andExpect(jsonPath("$.data").value(createdId));

        verify(studyDiaryService, times(1)).createStudyDiary(any(StudyDiaryCreateRequest.class), any());
    }

    @Test
    @DisplayName("POST /api/v1/studydiaries - 배움일기 생성 실패 (필수 필드 누락)")
    void createStudyDiary_Fail_MissingRequiredField() throws Exception {
        // given
        StudyDiaryCreateRequest invalidRequest = new StudyDiaryCreateRequest();

        // when & then
        mockMvc.perform(post("/api/v1/studydiaries")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(studyDiaryService, never()).createStudyDiary(any(), any());
    }

    @Test
    @DisplayName("PUT /api/v1/studydiaries/{id} - 배움일기 수정 성공")
    void updateStudyDiary_Success() throws Exception {
        // given
        Long diaryId = 1L;
        String updateJson = "{\"title\":\"수정된 제목\",\"content\":\"수정된 내용\"}";
        
        when(studyDiaryService.updateStudyDiary(eq(diaryId), any(StudyDiaryUpdateRequest.class), any())).thenReturn(diaryId);

        // when & then
        mockMvc.perform(put("/api/v1/studydiaries/{id}", diaryId)
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 수정되었습니다."))
                .andExpect(jsonPath("$.data").value(diaryId));

        verify(studyDiaryService, times(1)).updateStudyDiary(eq(diaryId), any(StudyDiaryUpdateRequest.class), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/studydiaries/{id} - 배움일기 삭제 성공")
    void deleteStudyDiary_Success() throws Exception {
        // given
        Long diaryId = 1L;
        doNothing().when(studyDiaryService).deleteStudyDiary(eq(diaryId), any());

        // when & then
        mockMvc.perform(delete("/api/v1/studydiaries/{id}", diaryId)
                        .with(user("testuser"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 삭제되었습니다."));

        verify(studyDiaryService, times(1)).deleteStudyDiary(eq(diaryId), any());
    }

    @Test
    @DisplayName("POST /api/v1/studydiaries/draft - 배움일기 임시 저장 성공")
    void saveDraft_Success() throws Exception {
        // given
        Long draftId = 1L;
        StudyDiaryCreateRequest request = new StudyDiaryCreateRequest();
        request.setTitle("임시 저장 제목");
        request.setContent("임시 저장 내용");

        when(studyDiaryService.saveDraft(any(StudyDiaryCreateRequest.class), any())).thenReturn(draftId);

        // when & then
        mockMvc.perform(post("/api/v1/studydiaries/draft")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 임시 저장되었습니다."))
                .andExpect(jsonPath("$.data").value(draftId));

        verify(studyDiaryService, times(1)).saveDraft(any(StudyDiaryCreateRequest.class), any());
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/user/{user-id}/week-status - 이번 주 작성 상태 조회 성공")
    void getWeekStatus_Success() throws Exception {
        // given
        Long userId = 1L;
        StudyDiaryWeekStatusResponse mockWeekStatus = StudyDiaryWeekStatusResponse.builder()
                .sunday(true)
                .monday(true)
                .tuesday(false)
                .wednesday(true)
                .thursday(true)
                .friday(false)
                .saturday(false)
                .todayStudyDiaryNum(2)
                .totalLike(15)
                .build();
        
        when(studyDiaryService.getWeekStatus(userId)).thenReturn(mockWeekStatus);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/user/{user-id}/week-status", userId)
                        .with(user("testuser")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("이번 주 작성 상태를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.todayStudyDiaryNum").value(2))
                .andExpect(jsonPath("$.data.totalLike").value(15));

        verify(studyDiaryService, times(1)).getWeekStatus(userId);
    }

    @Test
    @DisplayName("POST /api/v1/studydiaries/{id}/comments - 댓글 등록 성공")
    void createComment_Success() throws Exception {
        // given
        Long diaryId = 1L;
        Long commentId = 1L;
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("좋은 글이네요!")
                .build();

        when(studyDiaryService.createComment(eq(diaryId), any(CommentCreateRequest.class), any())).thenReturn(commentId);

        // when & then
        mockMvc.perform(post("/api/v1/studydiaries/{id}/comments", diaryId)
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 등록되었습니다."))
                .andExpect(jsonPath("$.data").value(commentId));

        verify(studyDiaryService, times(1)).createComment(eq(diaryId), any(CommentCreateRequest.class), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/studydiaries/{id}/comments/{commentId} - 댓글 삭제 성공")
    void deleteComment_Success() throws Exception {
        // given
        Long diaryId = 1L;
        Long commentId = 1L;
        doNothing().when(studyDiaryService).deleteComment(eq(diaryId), eq(commentId), any());

        // when & then
        mockMvc.perform(delete("/api/v1/studydiaries/{id}/comments/{commentId}", diaryId, commentId)
                        .with(user("testuser"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 삭제되었습니다."));

        verify(studyDiaryService, times(1)).deleteComment(eq(diaryId), eq(commentId), any());
    }

    @Test
    @DisplayName("POST /api/v1/studydiaries/{id}/like - 좋아요 토글 성공")
    void toggleLike_Success() throws Exception {
        // given
        Long diaryId = 1L;
        int totalLikes = 10;
        when(studyDiaryService.toggleLike(eq(diaryId), any())).thenReturn(totalLikes);

        // when & then
        mockMvc.perform(post("/api/v1/studydiaries/{id}/like", diaryId)
                        .with(user("testuser"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("좋아요 상태가 성공적으로 변경되었습니다."))
                .andExpect(jsonPath("$.data").value(totalLikes));

        verify(studyDiaryService, times(1)).toggleLike(eq(diaryId), any());
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/my/weeklyStatus - 나의 주간 활동 상황 조회 성공")
    void getWeeklyStatus_Success() throws Exception {
        // given
        StudyDiaryWeekStatusResponse mockWeekStatus = StudyDiaryWeekStatusResponse.builder()
                .sunday(true)
                .monday(false)
                .tuesday(true)
                .wednesday(false)
                .thursday(true)
                .friday(false)
                .saturday(false)
                .todayStudyDiaryNum(1)
                .totalLike(5)
                .build();
        
        when(studyDiaryService.getMyWeekStatus(any())).thenReturn(mockWeekStatus);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/my/weeklyStatus")
                        .with(user("testuser")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("현재 로그인한 사용자의 주간 활동 상황을 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.sunday").value(true))
                .andExpect(jsonPath("$.data.monday").value(false));

        verify(studyDiaryService, times(1)).getMyWeekStatus(any());
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/my/studyDiaries - 나의 글 목록 조회 성공")
    void getMyStudyDiaries_Success() throws Exception {
        // given
        List<StudyDiaryFindAllResponse> myDiaries = Arrays.asList(
                createMockStudyDiaryResponse(1L, "나의 글 1", "testuser"),
                createMockStudyDiaryResponse(2L, "나의 글 2", "testuser")
        );
        
        when(studyDiaryService.getMyStudyDiaries(any(), any(Pageable.class))).thenReturn(myDiaries);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/my/studyDiaries")
                        .with(user("testuser")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("현재 로그인한 사용자의 배움일기 목록을 성공적으로 조회했습니다."));

        verify(studyDiaryService, times(1)).getMyStudyDiaries(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/my/home - 최근 한달간 나의 배움일기 조회 성공")
    void getMyRecentStudyDiaries_Success() throws Exception {
        // given
        List<StudyDiaryMyHomeResponse> recentDiaries = Arrays.asList(
                createMockMyHomeResponse(1L, LocalDate.now()),
                createMockMyHomeResponse(2L, LocalDate.now().minusDays(1))
        );
        Page<StudyDiaryMyHomeResponse> mockPage = new PageImpl<>(recentDiaries);
        
        when(studyDiaryService.getMyRecentStudyDiaries(any(), any(Pageable.class))).thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/my/home")
                        .with(user("testuser")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("최근 한달간 배움일기를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(studyDiaryService, times(1)).getMyRecentStudyDiaries(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries - 인증되지 않은 사용자 접근 시 실패")
    void getStudyDiaries_Unauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/studydiaries"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(studyDiaryService, never()).getStudyDiaries(any());
    }

    // Helper Methods
    
    private StudyDiaryFindAllResponse createMockStudyDiaryResponse(Long id, String title, String name) {
        return createMockStudyDiaryResponse(id, title, name, 0, 0);
    }

    private StudyDiaryFindAllResponse createMockStudyDiaryResponse(Long id, String title, String name, int likeNum, int commentNum) {
        return StudyDiaryFindAllResponse.builder()
                .id(id)
                .name(name)
                .title(title)
                .content("테스트 내용")
                .likeNum(likeNum)
                .commentNum(commentNum)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private StudyDiaryDetailResponse createMockDetailResponse(Long id) {
        return StudyDiaryDetailResponse.builder()
                .id(id)
                .userId(1L)
                .title("상세 조회 테스트")
                .content("상세 내용")
                .name("testuser")
                .likeNum(5)
                .commentList(List.of())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private StudyDiaryMyHomeResponse createMockMyHomeResponse(Long id, LocalDate date) {
        return StudyDiaryMyHomeResponse.builder()
                .id(id)
                .title("홈 화면 글")
                .createdAt(date.atStartOfDay())
                .daysAgo(0L)
                .build();
    }
}