package org.example.hugmeexp.domain.studydiary.repository;

import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.domain.user.entity.User;
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
    @Query("SELECT s FROM StudyDiary s WHERE s.title LIKE %:keyword% OR s.content LIKE %:keyword% ORDER BY s.createdAt DESC")
    Page<StudyDiary> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    // 최신순 정렬 조회 (페이징)
    Page<StudyDiary> findByIsCreatedTrueOrderByCreatedAtDesc(Pageable pageable);

    // 임시저장 목록 조회 (페이징)
    @Query("SELECT s FROM StudyDiary s WHERE s.user.id = :userId AND s.isCreated = false")
    List<StudyDiary> findByIsCreatedFalse(@Param("userId") Long userId);


    // 좋아요순 정렬 조회 (페이징)  
    Page<StudyDiary> findAllByOrderByLikeCountDesc(Pageable pageable);

    // 특정 사용자의 이번 주 작성한 일기 조회
    @Query("SELECT s FROM StudyDiary s WHERE s.user.id = :userId AND s.createdAt BETWEEN :startOfWeek AND :endOfWeek")
    List<StudyDiary> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, 
                                                    @Param("startOfWeek") LocalDateTime startOfWeek, 
                                                    @Param("endOfWeek") LocalDateTime endOfWeek);

    // 사용자의 전체 일기 개수
    long countByUser_Id(Long userId);

    //사용자가 쓴 전체일기 조회
    @Query("SELECT s FROM StudyDiary s WHERE s.user.id = :findUserId ORDER BY s.createdAt DESC")
    List<StudyDiary> findByUser(Long findUserId);

    // 댓글과 함께 조회

    @Query("SELECT s FROM StudyDiary s LEFT JOIN FETCH s.comments WHERE s.id = :id")
    Optional<StudyDiary> findByIdWithComments(@Param("id") Long id);
    // 인기 일기 조회 (좋아요 많은 순)

    @Query("SELECT s FROM StudyDiary s WHERE s.likeCount >= :minLikes ORDER BY s.likeCount DESC")
    List<StudyDiary> findPopularStudyDiaries(@Param("minLikes") int minLikes, Pageable pageable);

    // 오늘 하루 인기 일기 조회 (오늘 작성된 글 중 좋아요 많은 순)
    @Query("SELECT s FROM StudyDiary s WHERE s.isCreated = true AND s.createdAt BETWEEN :startOfDay AND :endOfDay ORDER BY s.likeCount DESC")
    Page<StudyDiary> findTodayPopularStudyDiaries(@Param("startOfDay") LocalDateTime startOfDay, 
                                                  @Param("endOfDay") LocalDateTime endOfDay, 
                                                  Pageable pageable);

    // 이번 주 인기 일기 조회 (이번 주 작성된 글 중 좋아요 많은 순)
    @Query("SELECT s FROM StudyDiary s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.comments WHERE s.isCreated = true AND s.createdAt BETWEEN :startOfWeek AND :endOfWeek ORDER BY s.likeCount DESC LIMIT 50")
    List<StudyDiary> findWeeklyPopularStudyDiaries(@Param("startOfWeek") LocalDateTime startOfWeek,
                                                   @Param("endOfWeek") LocalDateTime endOfWeek);

    // 최근 한달간 특정 사용자의 배움일기 조회 (생성일 내림차순)
    @Query("SELECT s FROM StudyDiary s WHERE s.user.id = :userId AND s.isCreated = true AND s.createdAt >= :oneMonthAgo ORDER BY s.createdAt DESC")
    Page<StudyDiary> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(@Param("userId") Long userId, 
                                                                      @Param("oneMonthAgo") LocalDateTime oneMonthAgo, 
                                                                      Pageable pageable);
}