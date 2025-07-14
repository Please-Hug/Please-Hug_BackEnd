package org.example.hugmeexp.domain.bookmark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.bookmark.dto.request.BookmarkRequest;
import org.example.hugmeexp.domain.bookmark.dto.response.BookmarkResponse;
import org.example.hugmeexp.domain.bookmark.exception.BookmarkNotFoundException;
import org.example.hugmeexp.domain.bookmark.exception.BookmarkUserNotFoundException;
import org.example.hugmeexp.domain.bookmark.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@DisplayName("BookmarkController 테스트")
class BookmarkControllerTest {

    private static final String BASE_URL = "/api/v1/bookmark";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkService bookmarkService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookmarkRequest validRequest;
    private List<BookmarkResponse> bookmarkResponses;

    @BeforeEach
    void setUp() {
        validRequest = BookmarkRequest.builder()
                .title("테스트 북마크")
                .link("https://example.com")
                .build();

        bookmarkResponses = Arrays.asList(
                BookmarkResponse.builder()
                        .id(1L)
                        .title("테스트 북마크 1")
                        .link("https://example1.com")
                        .build(),
                BookmarkResponse.builder()
                        .id(2L)
                        .title("테스트 북마크 2")
                        .link("https://example2.com")
                        .build()
        );
    }

    @Nested
    @DisplayName("북마크 조회 API 테스트")
    class GetBookmarksTest {

        @Test
        @DisplayName("정상적으로 북마크 목록을 조회한다")
        @WithMockUser(username = "testuser")
        void getBookmarks_Success() throws Exception {
            // Given
            given(bookmarkService.getBookmarks("testuser")).willReturn(bookmarkResponses);

            // When & Then
            mockMvc.perform(get(BASE_URL)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].title").value("테스트 북마크 1"))
                    .andExpect(jsonPath("$.message").value("Bookmark status retrieved successfully"));
        }

        @Test
        @DisplayName("빈 북마크 목록을 정상적으로 조회한다")
        @WithMockUser(username = "testuser")
        void getBookmarks_EmptyList() throws Exception {
            // Given
            given(bookmarkService.getBookmarks("testuser")).willReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get(BASE_URL)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(0))
                    .andExpect(jsonPath("$.message").value("Bookmark status retrieved successfully"));
        }
    }

    @Nested
    @DisplayName("북마크 생성 API 테스트")
    class CreateBookmarkTest {

        @Test
        @DisplayName("정상적으로 북마크를 생성한다")
        @WithMockUser(username = "testuser")
        void createBookmark_Success() throws Exception {
            // Given
            doNothing().when(bookmarkService).createBookmark(anyString(), any(BookmarkRequest.class));

            // When & Then
            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("북마크를 추가했습니다."));
        }

        @Test
        @DisplayName("사용자를 찾을 수 없을 때 예외가 발생한다")
        @WithMockUser(username = "testuser")
        void createBookmark_UserNotFound() throws Exception {
            // Given
            doThrow(new BookmarkUserNotFoundException()).when(bookmarkService)
                    .createBookmark(anyString(), any(BookmarkRequest.class));

            // When & Then
            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("빈 제목으로 북마크 생성 시 400 에러")
        @WithMockUser(username = "testuser")
        void createBookmark_EmptyTitle() throws Exception {
            // Given
            BookmarkRequest emptyTitleRequest = BookmarkRequest.builder()
                    .title("")
                    .link("https://example.com")
                    .build();

            // When & Then
            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyTitleRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("null 제목으로 북마크 생성 시 400 에러")
        @WithMockUser(username = "testuser")
        void createBookmark_NullTitle() throws Exception {
            // Given
            BookmarkRequest nullTitleRequest = BookmarkRequest.builder()
                    .title(null)
                    .link("https://example.com")
                    .build();

            // When & Then
            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nullTitleRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("빈 링크로 북마크 생성 시 400 에러")
        @WithMockUser(username = "testuser")
        void createBookmark_EmptyLink() throws Exception {
            // Given
            BookmarkRequest emptyLinkRequest = BookmarkRequest.builder()
                    .title("테스트 북마크")
                    .link("")
                    .build();

            // When & Then
            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyLinkRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("null 링크로 북마크 생성 시 400 에러")
        @WithMockUser(username = "testuser")
        void createBookmark_NullLink() throws Exception {
            // Given
            BookmarkRequest nullLinkRequest = BookmarkRequest.builder()
                    .title("테스트 북마크")
                    .link(null)
                    .build();

            // When & Then
            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nullLinkRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("북마크 수정 API 테스트")
    class UpdateBookmarkTest {

        @Test
        @DisplayName("정상적으로 북마크를 수정한다")
        @WithMockUser(username = "testuser")
        void updateBookmark_Success() throws Exception {
            // Given
            Long bookmarkId = 1L;
            doNothing().when(bookmarkService).updateBookmark(anyString(), anyLong(), any(BookmarkRequest.class));

            // When & Then
            mockMvc.perform(put(BASE_URL + "/" + bookmarkId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("북마크를 수정했습니다."));
        }

        @Test
        @DisplayName("존재하지 않는 북마크 수정 시 404 반환")
        @WithMockUser(username = "testuser")
        void updateBookmark_NotFound() throws Exception {
            // Given
            Long bookmarkId = 999L;
            doThrow(new BookmarkNotFoundException()).when(bookmarkService)
                    .updateBookmark(anyString(), anyLong(), any(BookmarkRequest.class));

            // When & Then
            mockMvc.perform(put(BASE_URL + "/" + bookmarkId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("빈 제목으로 북마크 수정 시 400 에러")
        @WithMockUser(username = "testuser")
        void updateBookmark_EmptyTitle() throws Exception {
            // Given
            Long bookmarkId = 1L;
            BookmarkRequest emptyTitleRequest = BookmarkRequest.builder()
                    .title("")
                    .link("https://example.com")
                    .build();

            // When & Then
            mockMvc.perform(put(BASE_URL + "/" + bookmarkId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyTitleRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("빈 링크로 북마크 수정 시 400 에러")
        @WithMockUser(username = "testuser")
        void updateBookmark_EmptyLink() throws Exception {
            // Given
            Long bookmarkId = 1L;
            BookmarkRequest emptyLinkRequest = BookmarkRequest.builder()
                    .title("테스트 북마크")
                    .link("")
                    .build();

            // When & Then
            mockMvc.perform(put(BASE_URL + "/" + bookmarkId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyLinkRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("북마크 삭제 API 테스트")
    class DeleteBookmarkTest {

        @Test
        @DisplayName("정상적으로 북마크를 삭제한다")
        @WithMockUser(username = "testuser")
        void deleteBookmark_Success() throws Exception {
            // Given
            Long bookmarkId = 1L;
            doNothing().when(bookmarkService).deleteBookmark(anyString(), anyLong());

            // When & Then
            mockMvc.perform(delete(BASE_URL + "/" + bookmarkId)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("북마크를 삭제했습니다."));
        }

        @Test
        @DisplayName("존재하지 않는 북마크 삭제 시 404 반환")
        @WithMockUser(username = "testuser")
        void deleteBookmark_NotFound() throws Exception {
            // Given
            Long bookmarkId = 999L;
            doThrow(new BookmarkNotFoundException()).when(bookmarkService)
                    .deleteBookmark(anyString(), anyLong());

            // When & Then
            mockMvc.perform(delete(BASE_URL + "/" + bookmarkId)
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }
}