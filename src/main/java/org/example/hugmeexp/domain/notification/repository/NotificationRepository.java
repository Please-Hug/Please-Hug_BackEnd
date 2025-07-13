package org.example.hugmeexp.domain.notification.repository;

import org.example.hugmeexp.domain.notification.entity.Notification;
import org.example.hugmeexp.domain.notification.enums.NotificationType;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자의 알림 전체 조회(최신순)
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // 읽지 않은 알림 조회
    List<Notification> findByUserAndIsReadFalse(User user);

    // 특정 타입과 타겟 ID로 삭제 (댓글 ID, 다이어리 ID)
    void deleteByTargetIdAndType(Long targetId, NotificationType type);

    // 특정 사용자에 대한 반응 알림 삭제 ( 좋아요 취소 시 )
    void deleteByTargetIdAndTypeAndUser(Long targetId, NotificationType type, User user);

    List<Notification> findByTargetIdAndType(Long targetId, NotificationType type);

    List<Notification> findByTargetIdAndTypeAndUser(Long targetId, NotificationType type, User user);
}
