package org.example.hugmeexp.domain.studydiary.repository;

import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.global.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyDiaryRepository extends JpaRepository<StudyDiary, Long> {

    // 사용자별 배움일기 조회 (페이징)
    Page<StudyDiary> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // 사용자별 배움일기 조회 (페이징)
    Page<StudyDiary> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 제목이나 내용으로 검색 (페이징)
    @Query("SELECT s FROM StudyDiary s WHERE s.title LIKE %:keyword% OR s.Content LIKE %:keyword% ORDER BY s.createdAt DESC")
    Page<StudyDiary> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    // 최신순 정렬 조회 (페이징)
    Page<StudyDiary> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 좋아요순 정렬 조회 (페이징)  
    Page<StudyDiary> findAllByOrderByLikeDesc(Pageable pageable);

    // 특정 사용자의 이번 주 작성한 일기 조회
    @Query("SELECT s FROM StudyDiary s WHERE s.user.id = :userId AND s.createdAt BETWEEN :startOfWeek AND :endOfWeek")
    List<StudyDiary> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, 
                                                    @Param("startOfWeek") LocalDateTime startOfWeek, 
                                                    @Param("endOfWeek") LocalDateTime endOfWeek);

    // 사용자의 전체 일기 개수
    long countByUser_Id(Long userId);

    // 댓글과 함께 조회
    @Query("SELECT s FROM StudyDiary s LEFT JOIN FETCH s.comments WHERE s.id = :id")
    Optional<StudyDiary> findByIdWithComments(@Param("id") Long id);

    // 인기 일기 조회 (좋아요 많은 순)
    @Query("SELECT s FROM StudyDiary s WHERE s.like >= :minLikes ORDER BY s.like DESC")
    List<StudyDiary> findPopularStudyDiaries(@Param("minLikes") int minLikes, Pageable pageable);
} 