package org.example.hugmeexp.domain.studydiary.controller;

/*
 * ============================================================================
 * StudyDiary Controller Test Class
 * ============================================================================
 * 
 * 이 클래스는 StudyDiary 컨트롤러의 모든 엔드포인트를 테스트합니다.
 * 
 * 테스트 초보자를 위한 핵심 개념:
 * 
 * 1. 단위 테스트 (Unit Test)
 *    - 하나의 기능(메서드)을 독립적으로 테스트
 *    - 외부 의존성(데이터베이스, 네트워크 등)을 Mock으로 대체
 *    - 빠르고 안정적인 테스트 실행 가능
 * 
 * 2. Given-When-Then 패턴
 *    - Given: 테스트에 필요한 데이터와 상태 준비
 *    - When: 실제 테스트할 동작 실행
 *    - Then: 결과 검증 및 확인
 * 
 * 3. Mock 객체
 *    - 실제 객체를 대신하는 가짜 객체
 *    - 테스트에서 예상되는 동작을 미리 정의 가능
 *    - 외부 의존성 없이 독립적인 테스트 가능
 * 
 * 4. 테스트 어노테이션
 *    - @Test: 테스트 메서드 표시
 *    - @WebMvcTest: 웹 레이어만 테스트
 *    - @WithMockUser: 인증된 사용자로 테스트
 *    - @MockBean: Spring Boot에서 Mock 객체 생성
 * 
 * 5. 검증 방법
 *    - HTTP 상태 코드 검증 (200, 400, 401 등)
 *    - JSON 응답 내용 검증
 *    - Mock 객체 메서드 호출 검증
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

/**
 * StudyDiary 컨트롤러 테스트 클래스
 * 
 * @WebMvcTest: Spring MVC 컨트롤러 레이어만 테스트하는 어노테이션
 *              - 웹 관련 설정만 로드하므로 빠르게 테스트 가능
 *              - 실제 서비스나 리포지토리는 로드하지 않음
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("StudyDiary 컨트롤러 테스트")
class StudyDiaryControllerTest {

    /**
     * MockMvc: 실제 웹 서버를 띄우지 않고 HTTP 요청/응답을 시뮬레이션하는 도구
     * - 컨트롤러의 메서드를 직접 호출하지 않고 HTTP 요청을 통해 테스트
     * - 실제 HTTP 요청과 동일한 방식으로 테스트 가능
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper: Java 객체를 JSON으로, JSON을 Java 객체로 변환하는 도구
     * - 테스트에서 요청 본문을 JSON 문자열로 변환할 때 사용
     * - Spring Boot에서 기본 제공하는 Jackson 라이브러리의 객체
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @MockBean: Spring Boot 테스트에서 사용하는 Mock 객체 생성 어노테이션
     * - 실제 서비스 구현체 대신 가짜 객체를 주입
     * - 테스트에서 원하는 동작을 when().thenReturn()으로 정의 가능
     * - 컨트롤러 로직만 테스트하고 서비스 로직은 테스트에서 제외
     */
    @MockBean
    private StudyDiaryService studyDiaryService;

    /**
     * JWT 서비스도 Mock으로 처리 (보안 관련 의존성 해결)
     */

    /**
     * 배움일기 목록 조회 테스트
     * 
     * @Test: JUnit 5에서 테스트 메서드임을 나타내는 어노테이션
     * @DisplayName: 테스트 실행 시 표시될 이름 (한글로 작성 가능)
     * @WithMockUser: 인증된 사용자로 테스트를 실행하는 어노테이션
     *               - username을 지정하여 해당 사용자로 인증된 상태를 시뮬레이션
     */
    @Test
    @DisplayName("GET /api/v1/studydiaries - 배움일기 목록 조회 성공")
    void getStudyDiaries_Success() throws Exception {
        // given: 테스트에 필요한 데이터 준비 단계
        // Mock 데이터 생성 - 실제 데이터베이스 대신 가짜 데이터 사용
        List<StudyDiaryFindAllResponse> diaryList = Arrays.asList(
                createMockStudyDiaryResponse(1L, "제목1", "testuser"),
                createMockStudyDiaryResponse(2L, "제목2", "testuser2")
        );
        // 페이징 처리된 결과를 시뮬레이션 (PageImpl: Page 인터페이스의 구현체)
        Page<StudyDiaryFindAllResponse> mockPage = new PageImpl<>(diaryList, PageRequest.of(0, 10), 2);
        
        // when: Mock 객체의 동작 정의
        // studyDiaryService.getStudyDiaries()가 호출되면 mockPage를 반환하도록 설정
        // any(Pageable.class): 어떤 Pageable 객체가 와도 상관없다는 뜻
        when(studyDiaryService.getStudyDiaries(any(Pageable.class))).thenReturn(mockPage);

        // when & then: 실제 테스트 실행 및 검증
        mockMvc.perform(get("/api/v1/studydiaries")  // GET 요청 수행
                        .with(user("testuser"))      // 인증된 사용자 추가
                        .param("page", "0")           // 쿼리 파라미터 추가
                        .param("size", "10")
                        .param("sort", "createdAt,DESC"))
                .andDo(print())  // 요청/응답 정보를 콘솔에 출력 (디버깅용)
                // 응답 검증 시작
                .andExpect(status().isOk())  // HTTP 상태코드가 200(OK)인지 확인
                .andExpect(jsonPath("$.message").value("배움일기 목록을 성공적으로 조회했습니다."))  // JSON 응답의 message 필드 확인
                .andExpect(jsonPath("$.data.content").isArray())  // data.content가 배열인지 확인
                .andExpect(jsonPath("$.data.content[0].title").value("제목1"));  // 첫 번째 항목의 제목 확인

        // verify: Mock 객체의 메서드가 예상대로 호출되었는지 검증
        // studyDiaryService.getStudyDiaries()가 정확히 1번 호출되었는지 확인
        verify(studyDiaryService, times(1)).getStudyDiaries(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/studydiaries/today/popular - 오늘 하루 인기 배움일기 조회 성공")
    void getTodayPopularStudyDiaries_Success() throws Exception {
        // given
        List<StudyDiaryFindAllResponse> popularDiaries = Arrays.asList(
                createMockStudyDiaryResponse(1L, "인기글1", "author1", 100, 10),
                createMockStudyDiaryResponse(2L, "인기글2", "author2", 50, 5)
        );
        Page<StudyDiaryFindAllResponse> mockPage = new PageImpl<>(popularDiaries);
        
        when(studyDiaryService.getTodayPopularStudyDiaries(any(Pageable.class))).thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/v1/studydiaries/today/popular")
                        .with(user("testuser")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("오늘 하루 인기 배움일기를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.content[0].likeNum").value(100));

        verify(studyDiaryService, times(1)).getTodayPopularStudyDiaries(any(Pageable.class));
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

    /**
     * 배움일기 생성 테스트 (POST 요청)
     * 
     * POST 요청은 GET과 달리 요청 본문(body)에 데이터를 담아서 보냄
     * JSON 형태로 데이터를 전송하고, CSRF 토큰도 함께 전송해야 함
     */
    @Test
    @DisplayName("POST /api/v1/studydiaries - 배움일기 생성 성공")
    void createStudyDiary_Success() throws Exception {
        // given: 테스트 데이터 준비
        Long createdId = 1L;  // 생성된 배움일기의 ID (서비스에서 반환할 가짜 값)
        
        // 요청 객체 생성 - 실제 사용자가 보낼 데이터와 동일한 형태
        StudyDiaryCreateRequest request = new StudyDiaryCreateRequest();
        request.setTitle("Spring Boot 학습 일기");
        request.setContent("# Spring Boot 학습 정리\\n\\n## 오늘 배운 내용");

        // Mock 서비스가 createStudyDiary를 호출하면 createdId를 반환하도록 설정
        // any(): 어떤 타입의 객체가 와도 상관없다는 Mockito의 ArgumentMatcher
        when(studyDiaryService.createStudyDiary(any(StudyDiaryCreateRequest.class), any())).thenReturn(createdId);

        // when & then: POST 요청 실행 및 검증
        mockMvc.perform(post("/api/v1/studydiaries")  // POST 요청 수행
                        .with(user("testuser"))      // 인증된 사용자 추가
                        .with(csrf())  // CSRF 토큰 추가 (Spring Security에서 요구)
                        .contentType(MediaType.APPLICATION_JSON)  // 요청 본문이 JSON임을 명시
                        .content(objectMapper.writeValueAsString(request)))  // Java 객체를 JSON 문자열로 변환하여 요청 본문에 추가
                .andDo(print())  // 요청/응답 정보 출력
                .andExpect(status().isOk())  // 200 상태 코드 확인
                .andExpect(jsonPath("$.message").value("성공적으로 생성되었습니다."))  // 응답 메시지 확인
                .andExpect(jsonPath("$.data").value(createdId));  // 생성된 ID 확인

        // 서비스 메서드가 정확히 1번 호출되었는지 검증
        verify(studyDiaryService, times(1)).createStudyDiary(any(StudyDiaryCreateRequest.class), any());
    }

    /**
     * 배움일기 생성 실패 테스트 - 검증 실패 케이스
     * 
     * 실제 운영에서 발생할 수 있는 잘못된 요청을 테스트
     * 필수 필드가 누락된 경우 어떻게 처리되는지 확인
     */
    @Test
    @DisplayName("POST /api/v1/studydiaries - 배움일기 생성 실패 (필수 필드 누락)")
    void createStudyDiary_Fail_MissingRequiredField() throws Exception {
        // given: 잘못된 요청 데이터 준비
        StudyDiaryCreateRequest invalidRequest = new StudyDiaryCreateRequest();
        // title과 content를 설정하지 않음 (null 상태)
        // @NotNull 어노테이션에 의해 검증 실패가 발생할 것임

        // when & then: 잘못된 요청 실행 및 실패 검증
        mockMvc.perform(post("/api/v1/studydiaries")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());  // 400 Bad Request 상태 코드 확인

        // verify: 검증 실패로 인해 서비스 메서드가 호출되지 않았는지 확인
        // never(): 메서드가 한 번도 호출되지 않았음을 검증하는 Mockito 메서드
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

    /**
     * 인증 실패 테스트
     * 
     * @WithMockUser 어노테이션을 사용하지 않으면 인증되지 않은 상태가 됨
     * Spring Security가 적용된 엔드포인트에 인증 없이 접근할 때의 동작을 테스트
     */
    @Test
    @DisplayName("GET /api/v1/studydiaries - 인증되지 않은 사용자 접근 시 실패")
    void getStudyDiaries_Unauthorized() throws Exception {
        // when & then: 인증 없이 보호된 엔드포인트에 접근
        mockMvc.perform(get("/api/v1/studydiaries"))
                .andDo(print())
                .andExpect(status().isUnauthorized());  // 401 Unauthorized 상태 코드 확인

        // 인증 실패로 인해 서비스 메서드가 호출되지 않았는지 확인
        verify(studyDiaryService, never()).getStudyDiaries(any());
    }

    // ================= Helper Methods (헬퍼 메서드) =================
    // 테스트에서 반복적으로 사용되는 Mock 데이터 생성 메서드들
    // 코드 중복을 줄이고 테스트 가독성을 높이기 위해 분리
    
    /**
     * 기본 StudyDiaryFindAllResponse Mock 객체 생성
     * 좋아요와 댓글 수는 0으로 기본 설정
     */
    private StudyDiaryFindAllResponse createMockStudyDiaryResponse(Long id, String title, String name) {
        return createMockStudyDiaryResponse(id, title, name, 0, 0);
    }

    /**
     * StudyDiaryFindAllResponse Mock 객체 생성 (상세 버전)
     * 좋아요 수와 댓글 수를 직접 지정 가능
     */
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

    /**
     * StudyDiaryDetailResponse Mock 객체 생성
     * 상세 조회 테스트에서 사용
     */
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

    /**
     * StudyDiaryMyHomeResponse Mock 객체 생성
     * 홈 화면용 응답 데이터 테스트에서 사용
     */
    private StudyDiaryMyHomeResponse createMockMyHomeResponse(Long id, LocalDate date) {
        return StudyDiaryMyHomeResponse.builder()
                .id(id)
                .title("홈 화면 글")
                .createdAt(date.atStartOfDay())
                .daysAgo(0L)
                .build();
    }
}