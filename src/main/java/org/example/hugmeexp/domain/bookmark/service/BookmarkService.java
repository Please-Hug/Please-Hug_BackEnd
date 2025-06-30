package org.example.hugmeexp.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.bookmark.dto.BookmarkRequest;
import org.example.hugmeexp.domain.bookmark.dto.BookmarkResponse;
import org.example.hugmeexp.domain.bookmark.entity.Bookmark;
import org.example.hugmeexp.domain.bookmark.exception.BookmarkNotFoundException;
import org.example.hugmeexp.domain.bookmark.exception.BookmarkUserNotFoundException;
import org.example.hugmeexp.domain.bookmark.repository.BookmarkRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository     userRepository;

    /** 전체 북마크 조회 */
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getBookmarks(String username) {
        return bookmarkRepository
                .findAllByUser_Username(username).stream()
                .map(BookmarkResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /** 북마크 추가 */
    @Transactional
    public void createBookmark(String username, BookmarkRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(BookmarkUserNotFoundException::new);

        Bookmark b = Bookmark.builder()
                .user(user)
                .title(req.getTitle())
                .link(req.getLink())
                .build();

        bookmarkRepository.save(b);
    }

    /** 북마크 수정 */
    @Transactional
    public void updateBookmark(String username, Long id, BookmarkRequest req) {
        Bookmark b = bookmarkRepository
                .findByIdAndUser_Username(id, username)
                .orElseThrow(BookmarkNotFoundException::new);

        b.update(req.getTitle(), req.getLink());
    }

    /** 북마크 삭제 */
    @Transactional
    public void deleteBookmark(String username, Long id) {
        Bookmark b = bookmarkRepository
                .findByIdAndUser_Username(id, username)
                .orElseThrow(BookmarkNotFoundException::new);

        bookmarkRepository.delete(b);
    }
}