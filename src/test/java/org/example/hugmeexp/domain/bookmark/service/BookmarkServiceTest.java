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
        testUser = User.builder()
                .username("testuser")
                .build();

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
            // Given
            String username = "testuser";
            List<Bookmark> bookmarks = Arrays.asList(testBookmark1, testBookmark2);
            given(bookmarkRepository.findAllByUser_Username(username)).willReturn(bookmarks);

            // When
            List<BookmarkResponse> result = bookmarkService.getBookmarks(username);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("테스트 북마크 1");
            assertThat(result.get(1).getTitle()).isEqualTo("테스트 북마크 2");
            verify(bookmarkRepository).findAllByUser_Username(username);
        }

        @Test
        @DisplayName("존재하지 않는 사용자의 북마크를 조회하면 빈 목록을 반환한다")
        void getBookmarks_NoUser_ReturnsEmptyList() {
            // Given
            String username = "nonexistent";
            given(bookmarkRepository.findAllByUser_Username(username)).willReturn(Arrays.asList());

            // When
            List<BookmarkResponse> result = bookmarkService.getBookmarks(username);

            // Then
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
            // Given
            String username = "testuser";
            given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
            given(bookmarkRepository.save(any(Bookmark.class))).willReturn(testBookmark1);

            // When
            assertThatCode(() -> bookmarkService.createBookmark(username, validRequest))
                    .doesNotThrowAnyException();

            // Then
            verify(userRepository).findByUsername(username);
            verify(bookmarkRepository).save(any(Bookmark.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 북마크 생성 시 예외가 발생한다")
        void createBookmark_UserNotFound_ThrowsException() {
            // Given
            String username = "nonexistent";
            given(userRepository.findByUsername(username)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookmarkService.createBookmark(username, validRequest))
                    .isInstanceOf(BookmarkUserNotFoundException.class);

            verify(userRepository).findByUsername(username);
            verify(bookmarkRepository, never()).save(any(Bookmark.class));
        }

        @Test
        @DisplayName("null 요청으로 북마크 생성 시 예외가 발생한다")
        void createBookmark_NullRequest_ThrowsException() {
            // Given
            String username = "testuser";
            given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));

            // When & Then
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
            // Given
            String username = "testuser";
            Long bookmarkId = 1L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.of(testBookmark1));

            // When
            assertThatCode(() -> bookmarkService.updateBookmark(username, bookmarkId, validRequest))
                    .doesNotThrowAnyException();

            // Then
            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
        }

        @Test
        @DisplayName("존재하지 않는 북마크 수정 시 예외가 발생한다")
        void updateBookmark_BookmarkNotFound_ThrowsException() {
            // Given
            String username = "testuser";
            Long bookmarkId = 999L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookmarkService.updateBookmark(username, bookmarkId, validRequest))
                    .isInstanceOf(BookmarkNotFoundException.class);

            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
        }
    }

    @Nested
    @DisplayName("북마크 삭제 테스트")
    class DeleteBookmarkTest {

        @Test
        @DisplayName("정상적으로 북마크를 삭제한다")
        void deleteBookmark_Success() {
            // Given
            String username = "testuser";
            Long bookmarkId = 1L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.of(testBookmark1));

            // When
            assertThatCode(() -> bookmarkService.deleteBookmark(username, bookmarkId))
                    .doesNotThrowAnyException();

            // Then
            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
            verify(bookmarkRepository).delete(testBookmark1);
        }

        @Test
        @DisplayName("존재하지 않는 북마크 삭제 시 예외가 발생한다")
        void deleteBookmark_BookmarkNotFound_ThrowsException() {
            // Given
            String username = "testuser";
            Long bookmarkId = 999L;
            given(bookmarkRepository.findByIdAndUser_Username(bookmarkId, username))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookmarkService.deleteBookmark(username, bookmarkId))
                    .isInstanceOf(BookmarkNotFoundException.class);

            verify(bookmarkRepository).findByIdAndUser_Username(bookmarkId, username);
            verify(bookmarkRepository, never()).delete(any(Bookmark.class));
        }
    }
}