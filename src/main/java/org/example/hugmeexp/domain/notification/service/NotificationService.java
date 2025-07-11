package org.example.hugmeexp.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.notification.dto.NotificationDeleteDTO;
import org.example.hugmeexp.domain.notification.dto.NotificationResponseDTO;
import org.example.hugmeexp.domain.notification.entity.Notification;
import org.example.hugmeexp.domain.notification.enums.NotificationType;
import org.example.hugmeexp.domain.notification.exception.ForbiddenNotificationAccessException;
import org.example.hugmeexp.domain.notification.exception.NotificationNotFoundException;
import org.example.hugmeexp.domain.notification.repository.NotificationRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.common.sse.SseService;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@DependsOn("sseService") // SseService가 먼저 초기화되도록 보장
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    // 칭찬을 받았을 때 알림을 생성하고 SSE로 전송
    @Transactional
    public void sendPraiseNotification(User user, Long praiseId) {
        String content = NotificationType.PRAISE_RECEIVED.getDescription();
        createAndSend(user, NotificationType.PRAISE_RECEIVED, content, praiseId);
    }

    // 배움일기에 댓글이 달렸을 때 알림을 생성하고 SSE로 전송
    @Transactional
    public void sendDiaryCommentNotification(User user, String diaryTitle, Long targetId) {
        String title = diaryTitle == null ? "새 배움일기" : diaryTitle;
        String message = title + "에 댓글이 달렸어요";
        createAndSend(user, NotificationType.DIARY_COMMENT, message, targetId);
    }

    // 배움일기에 댓글이 삭제되었을 때 알림 제거
    @Transactional
    public void deleteDiaryCommentNotification(User user, Long targetId) {

        // 알림 엔티티 먼저 조회
        List<Notification> notifications = notificationRepository.findByTargetIdAndType(targetId, NotificationType.DIARY_COMMENT);

        // 삭제된 알림 정보를 프론트에 보내기 위해 DTO 변환 후 전송
        for(Notification notification : notifications) {
            NotificationDeleteDTO deleteDTO = NotificationDeleteDTO.from(notification);
            try {
                sseService.sendNotificationDeleted(user.getId(), deleteDTO);// sse로 삭제 알림
            } catch (Exception e){
                log.warn("Failed to send notification deletion via SSE for user {}: {}", user.getId(), notification.getId(), e);
            }
        }
        notificationRepository.deleteAll(notifications);
    }

    // 배움일기에 좋아요가 달렸을 때 알림을 생성하고 SSE로 전송
    @Transactional
    public void sendDiaryLikeNotification(User user, String diaryTitle, Long targetId) {
        String title = diaryTitle == null ? "새 배움일기" : diaryTitle;
        String message = title + "에 좋아요가 달렸어요";
        createAndSend(user, NotificationType.DIARY_LIKE, message, targetId);
    }

    // 배움일기에 좋아요가 취소되었을 때 알림 제거
    @Transactional
    public void deleteDiaryLikeNotification(User user, Long targetId) {
        // 알림 엔티티 먼저 조회
        List<Notification> notifications = notificationRepository.findByTargetIdAndTypeAndUser(targetId, NotificationType.DIARY_LIKE,user);

        // 삭제된 알림 정보를 프론트에 보내기 위해 DTO 변환 후 전송
        for(Notification notification : notifications) {
            NotificationDeleteDTO deleteDTO = NotificationDeleteDTO.from(notification);
            try {
                sseService.sendNotificationDeleted(user.getId(), deleteDTO); // sse로 삭제 알림
            } catch (Exception e) {
                log.warn("Failed to send notification deletion via SSE for user {}: {}", user.getId(), notification.getId(), e);
            }
        }
        notificationRepository.deleteAll(notifications);
    }

    // 배움일기 삭제 시 해당 일기에 대한 모든 알림 제거
    @Transactional
    public void deleteAllByDiaryId(User user, Long diaryId) {
        // 좋아요 알림 제거
        List<Notification> likeNotifications = notificationRepository.findByTargetIdAndTypeAndUser(diaryId, NotificationType.DIARY_LIKE,user);

        for(Notification notification : likeNotifications) {
            NotificationDeleteDTO deleteDTO = NotificationDeleteDTO.from(notification);
            try {
                sseService.sendNotificationDeleted(user.getId(), deleteDTO); // sse로 삭제 알림
            } catch (Exception e) {
                log.warn("Failed to send notification deletion via SSE for user {}: {}", user.getId(), notification.getId(), e);
            }
        }
        notificationRepository.deleteAll(likeNotifications);

        // 댓글 알림 제거
        List<Notification> commentNotifications = notificationRepository.findByTargetIdAndType(diaryId, NotificationType.DIARY_COMMENT);

        for(Notification notification : commentNotifications) {
            NotificationDeleteDTO deleteDTO = NotificationDeleteDTO.from(notification);
            try{
                sseService.sendNotificationDeleted(user.getId(), deleteDTO); // sse로 삭제 알림
            } catch (Exception e) {
                log.warn("Failed to send notification deletion via SSE for user {}: {}", user.getId(), notification.getId(), e);
            }
        }
        notificationRepository.deleteAll(commentNotifications);
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
        NotificationResponseDTO dto = NotificationResponseDTO.from(notification);

        try{
            sseService.sendNotification(user.getId(), dto);    // 실시간 전송
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
