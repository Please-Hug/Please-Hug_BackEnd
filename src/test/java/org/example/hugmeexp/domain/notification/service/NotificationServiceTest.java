package org.example.hugmeexp.domain.notification.service;

import org.example.hugmeexp.domain.notification.dto.NotificationDeleteDTO;
import org.example.hugmeexp.domain.notification.dto.NotificationResponseDTO;
import org.example.hugmeexp.domain.notification.entity.Notification;
import org.example.hugmeexp.domain.notification.enums.NotificationType;
import org.example.hugmeexp.domain.notification.repository.NotificationRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.common.sse.SseService;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SseService sseService;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Long testPraiseId;
    private Long testDiaryId;
    private String testDiaryTitle;

    @BeforeEach
    void setUp() {
        // 테스트 유저 설정
        testUser = createTestUser("testUser", "테스트유저", "010-1234-5678", 1L);
        testPraiseId = 100L;
        testDiaryId = 200L;
        testDiaryTitle = "테스트 배움일기";
    }

    // 테스트 유저 생성 헬퍼 메소드
    private User createTestUser(String username, String name, String phoneNumber, Long id) {
        User user = User.builder()
                .username(username)
                .password("password123")
                .phoneNumber(phoneNumber)
                .name(name)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @Test
    @DisplayName("칭찬 알림 생성 및 저장 테스트")
    void testSendPraiseNotification_SavesNotification() {
        // given
        Notification savedNotification = Notification.of(testUser, NotificationType.PRAISE_RECEIVED, 
                NotificationType.PRAISE_RECEIVED.getDescription(), testPraiseId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // when
        notificationService.sendPraiseNotification(testUser, testPraiseId);

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(testUser, capturedNotification.getUser());
        assertEquals(NotificationType.PRAISE_RECEIVED, capturedNotification.getType());
        assertEquals(NotificationType.PRAISE_RECEIVED.getDescription(), capturedNotification.getContent());
        assertEquals(testPraiseId, capturedNotification.getTargetId());
        assertFalse(capturedNotification.isRead());
    }

    @Test
    @DisplayName("칭찬 알림 SSE 전송 테스트")
    void testSendPraiseNotification_SendsSSE() {
        // given
        Notification savedNotification = Notification.of(testUser, NotificationType.PRAISE_RECEIVED, 
                NotificationType.PRAISE_RECEIVED.getDescription(), testPraiseId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // when
        notificationService.sendPraiseNotification(testUser, testPraiseId);

        // then
        verify(sseService).sendNotification(eq(testUser.getId()), any(NotificationResponseDTO.class));
    }

    @Test
    @DisplayName("칭찬 알림 SSE 전송 실패 시에도 알림은 저장됨")
    void testSendPraiseNotification_SavesNotificationEvenIfSSEFails() {
        // given
        Notification savedNotification = Notification.of(testUser, NotificationType.PRAISE_RECEIVED, 
                NotificationType.PRAISE_RECEIVED.getDescription(), testPraiseId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        doThrow(new RuntimeException("SSE 전송 실패")).when(sseService).sendNotification(any(), any());

        // when
        notificationService.sendPraiseNotification(testUser, testPraiseId);

        // then
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("배움일기 댓글 알림 생성 및 저장 테스트 - 정상 제목")
    void testSendDiaryCommentNotification_SavesNotification_WithValidTitle() {
        // given
        String expectedMessage = testDiaryTitle + "에 댓글이 달렸어요";
        Notification savedNotification = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                expectedMessage, testDiaryId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // when
        notificationService.sendDiaryCommentNotification(testUser, testDiaryTitle, testDiaryId);

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(testUser, capturedNotification.getUser());
        assertEquals(NotificationType.DIARY_COMMENT, capturedNotification.getType());
        assertEquals(expectedMessage, capturedNotification.getContent());
        assertEquals(testDiaryId, capturedNotification.getTargetId());
        assertFalse(capturedNotification.isRead());
    }

    @Test
    @DisplayName("배움일기 댓글 알림 생성 및 저장 테스트 - null 제목")
    void testSendDiaryCommentNotification_SavesNotification_WithNullTitle() {
        // given
        String defaultTitle = "새 배움일기";
        String expectedMessage = defaultTitle + "에 댓글이 달렸어요";
        Notification savedNotification = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                expectedMessage, testDiaryId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // when
        notificationService.sendDiaryCommentNotification(testUser, null, testDiaryId);

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(testUser, capturedNotification.getUser());
        assertEquals(NotificationType.DIARY_COMMENT, capturedNotification.getType());
        assertEquals(expectedMessage, capturedNotification.getContent());
        assertEquals(testDiaryId, capturedNotification.getTargetId());
        assertFalse(capturedNotification.isRead());
    }

    @Test
    @DisplayName("배움일기 댓글 알림 SSE 전송 테스트")
    void testSendDiaryCommentNotification_SendsSSE() {
        // given
        String expectedMessage = testDiaryTitle + "에 댓글이 달렸어요";
        Notification savedNotification = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                expectedMessage, testDiaryId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // when
        notificationService.sendDiaryCommentNotification(testUser, testDiaryTitle, testDiaryId);

        // then
        verify(sseService).sendNotification(eq(testUser.getId()), any(NotificationResponseDTO.class));
    }

    @Test
    @DisplayName("배움일기 댓글 알림 SSE 전송 실패 시에도 알림은 저장됨")
    void testSendDiaryCommentNotification_SavesNotificationEvenIfSSEFails() {
        // given
        String expectedMessage = testDiaryTitle + "에 댓글이 달렸어요";
        Notification savedNotification = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                expectedMessage, testDiaryId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        doThrow(new RuntimeException("SSE 전송 실패")).when(sseService).sendNotification(any(), any());

        // when
        notificationService.sendDiaryCommentNotification(testUser, testDiaryTitle, testDiaryId);

        // then
        verify(notificationRepository).save(any(Notification.class));
    }
    @Test
    @DisplayName("배움일기 좋아요 알림 생성 및 저장 테스트")
    void testSendDiaryLikeNotification_SavesNotification() {
        // given
        String expectedMessage = testDiaryTitle + "에 좋아요가 달렸어요";
        Notification savedNotification = Notification.of(testUser, NotificationType.DIARY_LIKE, 
                expectedMessage, testDiaryId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // when
        notificationService.sendDiaryLikeNotification(testUser, testDiaryTitle, testDiaryId);

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(testUser, capturedNotification.getUser());
        assertEquals(NotificationType.DIARY_LIKE, capturedNotification.getType());
        assertEquals(expectedMessage, capturedNotification.getContent());
        assertEquals(testDiaryId, capturedNotification.getTargetId());
        assertFalse(capturedNotification.isRead());
    }

    @Test
    @DisplayName("배움일기 좋아요 알림 SSE 전송 테스트")
    void testSendDiaryLikeNotification_SendsSSE() {
        // given
        String expectedMessage = testDiaryTitle + "에 좋아요가 달렸어요";
        Notification savedNotification = Notification.of(testUser, NotificationType.DIARY_LIKE, 
                expectedMessage, testDiaryId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // when
        notificationService.sendDiaryLikeNotification(testUser, testDiaryTitle, testDiaryId);

        // then
        verify(sseService).sendNotification(eq(testUser.getId()), any(NotificationResponseDTO.class));
    }

    @Test
    @DisplayName("배움일기 좋아요 알림 SSE 전송 실패 시에도 알림은 저장됨")
    void testSendDiaryLikeNotification_SavesNotificationEvenIfSSEFails() {
        // given
        String expectedMessage = testDiaryTitle + "에 좋아요가 달렸어요";
        Notification savedNotification = Notification.of(testUser, NotificationType.DIARY_LIKE, 
                expectedMessage, testDiaryId);
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        doThrow(new RuntimeException("SSE 전송 실패")).when(sseService).sendNotification(any(), any());

        // when
        notificationService.sendDiaryLikeNotification(testUser, testDiaryTitle, testDiaryId);

        // then
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("배움일기 댓글 알림 제거 및 SSE 삭제 전송 테스트")
    void testDeleteDiaryCommentNotification() {
        // given
        Notification notification1 = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                "테스트 배움일기에 댓글이 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification1, "id", 1L);

        Notification notification2 = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                "테스트 배움일기에 댓글이 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification2, "id", 2L);

        List<Notification> notifications = List.of(notification1, notification2);

        when(notificationRepository.findByTargetIdAndType(testDiaryId, NotificationType.DIARY_COMMENT))
                .thenReturn(notifications);

        // when
        notificationService.deleteDiaryCommentNotification(testUser, testDiaryId);

        // then
        // 알림 조회 확인
        verify(notificationRepository).findByTargetIdAndType(testDiaryId, NotificationType.DIARY_COMMENT);

        // SSE 삭제 전송 확인 (각 알림마다 한 번씩)
        verify(sseService, times(2)).sendNotificationDeleted(eq(testUser.getId()), any(NotificationDeleteDTO.class));

        // 알림 삭제 확인
        verify(notificationRepository).deleteAll(notifications);
    }

    @Test
    @DisplayName("배움일기 좋아요 알림 제거 - 사용자 기반 + 타입 기반 삭제 확인")
    void testDeleteDiaryLikeNotification() {
        // given
        Notification notification1 = Notification.of(testUser, NotificationType.DIARY_LIKE, 
                "테스트 배움일기에 좋아요가 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification1, "id", 1L);

        List<Notification> notifications = List.of(notification1);

        when(notificationRepository.findByTargetIdAndTypeAndUser(testDiaryId, NotificationType.DIARY_LIKE, testUser))
                .thenReturn(notifications);

        // when
        notificationService.deleteDiaryLikeNotification(testUser, testDiaryId);

        // then
        // 사용자 기반 + 타입 기반 조회 확인
        verify(notificationRepository).findByTargetIdAndTypeAndUser(testDiaryId, NotificationType.DIARY_LIKE, testUser);

        // SSE 삭제 전송 확인
        verify(sseService).sendNotificationDeleted(eq(testUser.getId()), any(NotificationDeleteDTO.class));

        // 알림 삭제 확인
        verify(notificationRepository).deleteAll(notifications);
    }

    @Test
    @DisplayName("배움일기 삭제 시 모든 관련 알림 제거 - 좋아요, 댓글 각각 삭제")
    void testDeleteAllByDiaryId() {
        // given
        // 좋아요 알림
        Notification likeNotification = Notification.of(testUser, NotificationType.DIARY_LIKE, 
                "테스트 배움일기에 좋아요가 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(likeNotification, "id", 1L);
        List<Notification> likeNotifications = List.of(likeNotification);

        // 댓글 알림
        Notification commentNotification = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                "테스트 배움일기에 댓글이 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(commentNotification, "id", 2L);
        List<Notification> commentNotifications = List.of(commentNotification);

        when(notificationRepository.findByTargetIdAndTypeAndUser(testDiaryId, NotificationType.DIARY_LIKE, testUser))
                .thenReturn(likeNotifications);
        when(notificationRepository.findByTargetIdAndType(testDiaryId, NotificationType.DIARY_COMMENT))
                .thenReturn(commentNotifications);

        // when
        notificationService.deleteAllByDiaryId(testUser, testDiaryId);

        // then
        // 좋아요 알림 조회 및 삭제 확인
        verify(notificationRepository).findByTargetIdAndTypeAndUser(testDiaryId, NotificationType.DIARY_LIKE, testUser);
        verify(notificationRepository).deleteAll(likeNotifications);

        // 댓글 알림 조회 및 삭제 확인
        verify(notificationRepository).findByTargetIdAndType(testDiaryId, NotificationType.DIARY_COMMENT);
        verify(notificationRepository).deleteAll(commentNotifications);

        // 각 알림에 대해 sendNotificationDeleted가 호출되므로 총 2번 호출됨을 검증
        verify(sseService, times(2)).sendNotificationDeleted(eq(testUser.getId()), any(NotificationDeleteDTO.class));
    }

    @Test
    @DisplayName("레벨업 알림 생성 - 레벨에 따른 메시지 정확성 확인")
    void testSendLevelUpNotification() {
        // given
        int level = 3;
        String expectedMessage = "레벨 " + level + "로 업그레이드 되었어요!";
        Notification savedNotification = Notification.of(testUser, NotificationType.LEVEL_UP, 
                expectedMessage, testUser.getId());
        ReflectionTestUtils.setField(savedNotification, "id", 1L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // when
        notificationService.sendLevelUpNotification(testUser, level, testUser.getId());

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(testUser, capturedNotification.getUser());
        assertEquals(NotificationType.LEVEL_UP, capturedNotification.getType());
        assertEquals(expectedMessage, capturedNotification.getContent());
        assertEquals(testUser.getId(), capturedNotification.getTargetId());

        // SSE 전송 확인
        verify(sseService).sendNotification(eq(testUser.getId()), any(NotificationResponseDTO.class));
    }

    @Test
    @DisplayName("내 알림 목록 조회 - 정렬 순서, DTO 변환 확인")
    void testGetMyNotifications() {
        // given
        Notification notification1 = Notification.of(testUser, NotificationType.DIARY_LIKE, 
                "테스트 배움일기에 좋아요가 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification1, "id", 1L);

        Notification notification2 = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                "테스트 배움일기에 댓글이 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification2, "id", 2L);

        List<Notification> notifications = List.of(notification1, notification2);

        when(notificationRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(notifications);

        // CustomUserDetails 생성
        org.example.hugmeexp.global.security.CustomUserDetails userDetails = 
                new org.example.hugmeexp.global.security.CustomUserDetails(testUser);

        // when
        List<NotificationResponseDTO> result = notificationService.getMyNotifications(userDetails);

        // then
        // 정렬 순서로 조회 확인
        verify(notificationRepository).findByUserOrderByCreatedAtDesc(testUser);

        // DTO 변환 확인
        assertEquals(2, result.size());
        assertEquals(notification1.getId(), result.get(0).getId());
        assertEquals(notification1.getContent(), result.get(0).getContent());
        assertEquals(notification1.getType().name(), result.get(0).getType());
        assertEquals(notification2.getId(), result.get(1).getId());
        assertEquals(notification2.getContent(), result.get(1).getContent());
        assertEquals(notification2.getType().name(), result.get(1).getType());
    }

    @Test
    @DisplayName("알림 읽음 처리 - 정상 케이스")
    void testMarkAsRead_Success() {
        // given
        Long notificationId = 1L;
        Notification notification = Notification.of(testUser, NotificationType.DIARY_LIKE, 
                "테스트 배움일기에 좋아요가 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification, "id", notificationId);

        when(notificationRepository.findById(notificationId)).thenReturn(java.util.Optional.of(notification));

        // CustomUserDetails 생성
        org.example.hugmeexp.global.security.CustomUserDetails userDetails = 
                new org.example.hugmeexp.global.security.CustomUserDetails(testUser);

        // when
        notificationService.markAsRead(notificationId, userDetails);

        // then
        // 알림 존재 여부 확인
        verify(notificationRepository).findById(notificationId);

        // 상태 변경 확인
        assertTrue(notification.isRead());
    }

    @Test
    @DisplayName("알림 읽음 처리 - 알림이 존재하지 않는 경우")
    void testMarkAsRead_NotificationNotFound() {
        // given
        Long notificationId = 999L;

        when(notificationRepository.findById(notificationId)).thenReturn(java.util.Optional.empty());

        // CustomUserDetails 생성
        org.example.hugmeexp.global.security.CustomUserDetails userDetails = 
                new org.example.hugmeexp.global.security.CustomUserDetails(testUser);

        // when & then
        assertThrows(org.example.hugmeexp.domain.notification.exception.NotificationNotFoundException.class, 
                () -> notificationService.markAsRead(notificationId, userDetails));
    }

    @Test
    @DisplayName("알림 읽음 처리 - 소유자가 아닌 경우")
    void testMarkAsRead_ForbiddenAccess() {
        // given
        Long notificationId = 1L;
        User otherUser = createTestUser("otherUser", "다른유저", "010-9876-5432", 2L);
        Notification notification = Notification.of(otherUser, NotificationType.DIARY_LIKE, 
                "테스트 배움일기에 좋아요가 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification, "id", notificationId);

        when(notificationRepository.findById(notificationId)).thenReturn(java.util.Optional.of(notification));

        // CustomUserDetails 생성
        org.example.hugmeexp.global.security.CustomUserDetails userDetails = 
                new org.example.hugmeexp.global.security.CustomUserDetails(testUser);

        // when & then
        assertThrows(org.example.hugmeexp.domain.notification.exception.ForbiddenNotificationAccessException.class, 
                () -> notificationService.markAsRead(notificationId, userDetails));
    }

    @Test
    @DisplayName("모든 알림 읽음 처리 - 읽지 않은 알림이 있는 경우")
    void testMarkAllAsRead_WithUnreadNotifications() {
        // given
        Notification notification1 = Notification.of(testUser, NotificationType.DIARY_LIKE, 
                "테스트 배움일기에 좋아요가 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification1, "id", 1L);

        Notification notification2 = Notification.of(testUser, NotificationType.DIARY_COMMENT, 
                "테스트 배움일기에 댓글이 달렸어요", testDiaryId);
        ReflectionTestUtils.setField(notification2, "id", 2L);

        List<Notification> unreadNotifications = List.of(notification1, notification2);

        when(notificationRepository.findByUserAndIsReadFalse(testUser))
                .thenReturn(unreadNotifications);

        // CustomUserDetails 생성
        org.example.hugmeexp.global.security.CustomUserDetails userDetails = 
                new org.example.hugmeexp.global.security.CustomUserDetails(testUser);

        // when
        notificationService.markAllAsRead(userDetails);

        // then
        // 읽지 않은 알림 조회 확인
        verify(notificationRepository).findByUserAndIsReadFalse(testUser);

        // 모든 알림이 읽음 처리되었는지 확인
        assertTrue(notification1.isRead());
        assertTrue(notification2.isRead());
    }

    @Test
    @DisplayName("모든 알림 읽음 처리 - 읽지 않은 알림이 없는 경우")
    void testMarkAllAsRead_WithNoUnreadNotifications() {
        // given
        when(notificationRepository.findByUserAndIsReadFalse(testUser))
                .thenReturn(List.of());

        // CustomUserDetails 생성
        org.example.hugmeexp.global.security.CustomUserDetails userDetails = 
                new org.example.hugmeexp.global.security.CustomUserDetails(testUser);

        // when
        notificationService.markAllAsRead(userDetails);

        // then
        // 읽지 않은 알림 조회 확인
        verify(notificationRepository).findByUserAndIsReadFalse(testUser);
        // 추가 작업이 없어야 함
    }
}
