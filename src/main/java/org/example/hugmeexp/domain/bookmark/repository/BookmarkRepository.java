package org.example.hugmeexp.domain.bookmark.repository;

import org.example.hugmeexp.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findAllByUser_Username(String username);

    Optional<Bookmark> findByIdAndUser_Username(Long id, String username);

}