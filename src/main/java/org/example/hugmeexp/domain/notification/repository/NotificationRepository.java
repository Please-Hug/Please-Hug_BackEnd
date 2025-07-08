package org.example.hugmeexp.domain.notification.repository;

import org.example.hugmeexp.domain.notification.entity.Notification;
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
}
