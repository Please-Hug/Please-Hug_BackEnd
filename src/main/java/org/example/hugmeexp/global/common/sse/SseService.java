package org.example.hugmeexp.global.common.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.notification.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final EmitterRepository emitterRepository;
    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L; // 30분

    /** SSE 구독 - 클라이언트가 서버에 연결 요청할 때 호출 됨
        * SSE mitter를 생성하고, 해당 Emitter를 저장소에 등록, 타임 아웃 설정
        * @param userId 구독할 사용자 ID
     */
    public SseEmitter subscribe(Long userId) {
        // 기본 timeout 설정 : 30분
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);

        // 연결 완료 및 콜백 등록
        // Emitter가 완료( 모든 데이터가 성공적으로 전송한 상태 ) emitter 삭제
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for userId: {}", userId);
            emitterRepository.delete(userId);
        });
        // Emitter가 타임아웃( 지정된 시간 동안 어떠한 이벤트도 전송되지 않은 상태 ) emitter 삭제
        emitter.onTimeout(() -> {
            log.info("SSE connection timed out for userId: {}", userId);
            emitterRepository.delete(userId);
        });
        // Emitter가 에러 발생 시 emitter 삭제
        emitter.onError(e -> {
            log.error("SSE connection error for userId: {}", userId, e);
            emitterRepository.delete(userId);
        });

        // 연결 확인 용 더미 이벤트 전송
        try{
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("SSE connection established for userId: " + userId));
        } catch (IOException e){
            log.warn("SSE connection error for userId: {}", userId, e);
        }

        return emitter;
    }

    /** SSE 알림 전송 - 특정 사용자에게 알림을 전송
        * @param userId 알림을 받을 사용자 ID
        * @param notification 전송할 알림 객체
     */
    public void sendNotification(Long userId, Notification notification) {
        SseEmitter emitter = emitterRepository.get(userId);
        if (emitter != null) {
            try{
                // 알림 이벤트 전송
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                log.info("SSE notification sent to userId: {}", userId, notification.getContent());
            } catch (IOException e) {
                log.error("Failed to send SSE notification to userId: {}", userId, e);
                // 전송 실패 시 Emitter 삭제
                emitterRepository.delete(userId);
            }
        } else {
            log.warn("No SSE connection found for userId: {}", userId);
        }
    }
}
