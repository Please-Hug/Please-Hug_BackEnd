package org.example.hugmeexp.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.notification.dto.NotificationResponseDTO;
import org.example.hugmeexp.domain.notification.entity.Notification;
import org.example.hugmeexp.domain.notification.enums.NotificationType;
import org.example.hugmeexp.domain.notification.exception.ForbiddenNotificationAccessException;
import org.example.hugmeexp.domain.notification.exception.NotificationNotFoundException;
import org.example.hugmeexp.domain.notification.repository.NotificationRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.common.sse.SseService;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    // 칭찬을 받았을 때 알림을 생성하고 SSE로 전송
    @Transactional
    public void sendPraiseNotification(User user, Long praiseId) {
        String content = NotificationType.PRAISE_RECEIVED.getDescription();
        Notification notification = Notification.of(user, NotificationType.PRAISE_RECEIVED, content, praiseId);
        notificationRepository.save(notification);
        sseService.sendNotification(user.getId(), notification);
    }

    // 배움일기에 댓글이 달렸을 때 알림을 생성하고 SSE로 전송
    @Transactional
    public void sendDiaryCommentNotification(User user, String diaryTitle, Long targetId) {
        String title = diaryTitle == null ? "새 배움일기" : diaryTitle;
        String message = title + "에 댓글이 달렸어요";
        createAndSend(user, NotificationType.DIARY_COMMENT, message, targetId);
    }

    // 배움일기에 좋아요가 달렸을 때 알림을 생성하고 SSE로 전송
    @Transactional
    public void sendDiaryLikeNotification(User user, String diaryTitle, Long targetId) {
        String title = diaryTitle == null ? "새 배움일기" : diaryTitle;
        String message = title + "에 좋아요가 달렸어요";
        createAndSend(user, NotificationType.DIARY_LIKE, message, targetId);
    }

    // 레벨 업 했을 때 알림을 생성하고 SSE로 전송
    @Transactional
    public void sendLevelUpNotification(User user, int level, Long targetId) {
        String message = "레벨 " + level + "로 업그레이드 되었어요!";
        createAndSend(user, NotificationType.LEVEL_UP, message, targetId);
    }

    private void createAndSend(User user, NotificationType type, String message, Long targetId) {
        Notification notification = Notification.of(user, type, message, targetId);
        notificationRepository.save(notification);

        try{
            sseService.sendNotification(user.getId(), notification);    // 실시간 전송
        } catch (Exception e) {
            log.warn("Failed to send notification via SSE for user {}: {}", user.getId(), notification.getId(), e);
            // 예외 발생 시에도 알림은 저장되지만, SSE 전송 실패로 로그에 경고 메시지 출력 -> 사용자가 나중에 확인 할 수 있다
        }

    }

    // 내 알림 목록 조회
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getMyNotifications(CustomUserDetails user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user.getUser())
                .stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId, CustomUserDetails user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException());
        if (!notification.getUser().getId().equals(user.getUser().getId())) {
            throw new ForbiddenNotificationAccessException();
        }
        notification.markAsRead();
    }

    // 모든 알림 읽음 처리
    @Transactional
    public void markAllAsRead(CustomUserDetails user) {
        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalse(user.getUser());
        if (notifications.isEmpty()) {
            return; // 읽지 않은 알림이 없으면 그냥 리턴
        }
        for (Notification notification : notifications) {
            notification.markAsRead();
        }
    }
}
