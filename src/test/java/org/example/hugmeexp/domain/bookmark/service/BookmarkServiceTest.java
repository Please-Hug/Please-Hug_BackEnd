package org.example.hugmeexp.domain.bookmark.service;

import org.example.hugmeexp.domain.bookmark.dto.request.BookmarkRequest;
import org.example.hugmeexp.domain.bookmark.dto.response.BookmarkResponse;
import org.example.hugmeexp.domain.bookmark.entity.Bookmark;
import org.example.hugmeexp.domain.bookmark.exception.BookmarkNotFoundException;
import org.example.hugmeexp.domain.bookmark.exception.BookmarkUserNotFoundException;
import org.example.hugmeexp.domain.bookmark.repository.BookmarkRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookmarkService 테스트")
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    private User testUser;
    private Bookmark testBookmark1;
    private Bookmark testBookmark2;
    private BookmarkRequest validRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성 (username만 사용)
        testUser = User.builder()
                .username("testuser")
                .build();

        // 테스트용 북마크 생성
        testBookmark1 = Bookmark.builder()
                .id(1L)
                .user(testUser)
                .title("테스트 북마크 1")
                .link("https://example1.com")
                .build();

        testBookmark2 = Bookmark.builder()
                .id(2L)
                .user(testUser)
                .title("테스트 북마크 2")
                .link("https://example2.com")
                .build();

        // 유효한 요청 객체 생성
        validRequest = BookmarkRequest.builder()
                .title("새 북마크")
                .link("https://newbookmark.com")
                .build();
    }

    @Nested
    @DisplayName("북마크 조회 테스트")
    class GetBookmarksTest {

        @Test
        @DisplayName("정상적으로 사용자의 북마크 목록을 조회한다")
        void getBookmarks_Success() {
            // Given: 사용자가 존재하고 북마크 목록이 있는 상황
            String username = "testuser";
            List<Bookmark> bookmarks = Arrays.asList(testBookmark1, testBookmark2);
            given(bookmarkRepository.findAllByUser_Username(username)).willReturn(bookmarks);

            // When: 북마크 목록을 조회
            List<BookmarkResponse> result = bookmarkService.getBookmarks(username);

            // Then: 정상적으로 북마크 목록이 반환된다
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("테스트 북마크 1");
            assertThat(result.get(1).getTitle()).isEqualTo("테스트 북마크 2");
            verify(bookmarkRepository).findAllByUser_Username(username);
        }

        @Test
        @DisplayName("존재하지 않는 사용자의 북마크를 조회하면 빈 목록을 반환한다")
        void getBookmarks_NoUser_ReturnsEmptyList() {
            // Given: 존재하지 않는 사용자
            String username = "nonexistent";
            given(bookmarkRepository.findAllByUser_Username(username)).willReturn(Arrays.asList());

            // When: 북마크 목록을 조회
            List<BookmarkResponse> result = bookmarkService.getBookmarks(username);

            // Then: 빈 목록이 반환된다
            assertThat(result).isEmpty();
            verify(bookmarkRepository).findAllByUser_Username(username);
        }
    }

    @Nested
    @DisplayName("북마크 생성 테스트")
    class CreateBookmarkTest {

        @Test
        @DisplayName("정상적으로 북마크를 생성한다")
        void createBookmark_Success() {
            // Given: 유효한 사용자와 요청 데이터
            String username = "testuser";
            given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
            given(bookmarkRepository.save(any(Bookmark.class))).willReturn(testBookmark1);

            // When: 북마크를 생성
            assertThatCode(() -> bookmarkService.createBookmark(username, validRequest))
                    .doesNotThrowAnyException();

            // Then: 저장 메서드가 호출된다
            verify(userRepository).findByUsername(username);
            verify(bookmarkRepository).save(any(Bookmark.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 북마크 생성 시 예외가 발생한다")
        void createBookmark_UserNotFound_ThrowsException() {
            // Given: 존재하지 않는 사용자
            String username = "nonexistent";
            given(userRepository.findByUsername(username)).willReturn(Optional.empty());

            // When & Then: 예외가 발생한다
            assertThatThrownBy(() -> bookmarkService.createBookmark(username, validRequest))
                    .isInstanceOf(BookmarkUserNotFoundException.class);

            verify(userRepository).findByUsername(username);
            verify(bookmarkRepository, never()).save(any(Bookmark.class));
        }

        @Test
        @DisplayName("null 요청으로 북마크 생성 시 예외가 발생한다")
        void createBookmark_NullRequest_ThrowsException() {
            // Given: null 요청
            String username = "testuser";
            given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));

            // When & Then: 예외가 발생한다
            assertThatThrownBy(() -> bookmarkService.createBookmark(username, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("북마크 수정 테스트")
    class UpdateBookmarkTest {

        @Test
        @DisplayName("정상적으로 북마크를 수정한다")
        void updateBookmark_Success() {
            // Given: 유효한 북마크와 요청 데이터
            String username = "testuser";
            Long bookmarkId = 1L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.of(testBookmark1));

            // When: 북마크를 수정
            assertThatCode(() -> bookmarkService.updateBookmark(username, bookmarkId, validRequest))
                    .doesNotThrowAnyException();

            // Then: 수정 메서드가 호출된다
            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
        }

        @Test
        @DisplayName("존재하지 않는 북마크 수정 시 예외가 발생한다")
        void updateBookmark_BookmarkNotFound_ThrowsException() {
            // Given: 존재하지 않는 북마크
            String username = "testuser";
            Long bookmarkId = 999L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.empty());

            // When & Then: 예외가 발생한다
            assertThatThrownBy(() -> bookmarkService.updateBookmark(username, bookmarkId, validRequest))
                    .isInstanceOf(BookmarkNotFoundException.class);

            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
        }

        @Test
        @DisplayName("빈 제목으로 북마크 수정 시 예외가 발생한다")
        void updateBookmark_EmptyTitle_ThrowsException() {
            // Given: 빈 제목을 가진 요청
            String username = "testuser";
            Long bookmarkId = 1L;
            BookmarkRequest invalidRequest = BookmarkRequest.builder()
                    .title("")
                    .link("https://example.com")
                    .build();

            // When & Then: 예외가 발생한다
            assertThatThrownBy(() -> bookmarkService.updateBookmark(username, bookmarkId, invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("북마크 제목은 필수입니다");
        }

        @Test
        @DisplayName("null 제목으로 북마크 수정 시 예외가 발생한다")
        void updateBookmark_NullTitle_ThrowsException() {
            // Given: null 제목을 가진 요청
            String username = "testuser";
            Long bookmarkId = 1L;
            BookmarkRequest invalidRequest = BookmarkRequest.builder()
                    .title(null)
                    .link("https://example.com")
                    .build();

            // When & Then: 예외가 발생한다
            assertThatThrownBy(() -> bookmarkService.updateBookmark(username, bookmarkId, invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("북마크 제목은 필수입니다");
        }

        @Test
        @DisplayName("빈 링크로 북마크 수정 시 예외가 발생한다")
        void updateBookmark_EmptyLink_ThrowsException() {
            // Given: 빈 링크를 가진 요청
            String username = "testuser";
            Long bookmarkId = 1L;
            BookmarkRequest invalidRequest = BookmarkRequest.builder()
                    .title("제목")
                    .link("  ")
                    .build();

            // When & Then: 예외가 발생한다
            assertThatThrownBy(() -> bookmarkService.updateBookmark(username, bookmarkId, invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("북마크 링크는 필수입니다");
        }
    }

    @Nested
    @DisplayName("북마크 삭제 테스트")
    class DeleteBookmarkTest {

        @Test
        @DisplayName("정상적으로 북마크를 삭제한다")
        void deleteBookmark_Success() {
            // Given: 유효한 북마크
            String username = "testuser";
            Long bookmarkId = 1L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.of(testBookmark1));

            // When: 북마크를 삭제
            assertThatCode(() -> bookmarkService.deleteBookmark(username, bookmarkId))
                    .doesNotThrowAnyException();

            // Then: 삭제 메서드가 호출된다
            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
            verify(bookmarkRepository).delete(testBookmark1);
        }

        @Test
        @DisplayName("존재하지 않는 북마크 삭제 시 예외가 발생한다")
        void deleteBookmark_BookmarkNotFound_ThrowsException() {
            // Given: 존재하지 않는 북마크
            String username = "testuser";
            Long bookmarkId = 999L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.empty());

            // When & Then: 예외가 발생한다
            assertThatThrownBy(() -> bookmarkService.deleteBookmark(username, bookmarkId))
                    .isInstanceOf(BookmarkNotFoundException.class);

            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
            verify(bookmarkRepository, never()).delete(any(Bookmark.class));
        }

        @Test
        @DisplayName("다른 사용자의 북마크 삭제 시 예외가 발생한다")
        void deleteBookmark_UnauthorizedUser_ThrowsException() {
            // Given: 다른 사용자의 북마크
            String username = "anotheruser";
            Long bookmarkId = 1L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.empty());

            // When & Then: 예외가 발생한다
            assertThatThrownBy(() -> bookmarkService.deleteBookmark(username, bookmarkId))
                    .isInstanceOf(BookmarkNotFoundException.class);

            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
            verify(bookmarkRepository, never()).delete(any(Bookmark.class));
        }
    }
}