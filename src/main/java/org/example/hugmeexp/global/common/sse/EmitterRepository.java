package org.example.hugmeexp.global.common.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    // 사용자 Id 기준으로 emitter를 저장하는 맵
    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 사용자 id 로 Emitter 저장하기
    public void save(Long userId, SseEmitter emitter) {
        emitterMap.put(userId, emitter);
    }

    // 등록된 Emitter 조회
    public SseEmitter get(Long userId) {
        return emitterMap.get(userId);
    }

    // 연결 해제 시 Emitter 삭제
    // 예를 들어 클라이언트가 연결을 끊었을 때 호출
    // 또는 서버에서 해당 유저의 SSE 연결을 종료할 때 사용
    public void delete(Long userId) {
        emitterMap.remove(userId);
    }

    // 유저 id와 연결된 Emitter가 존재하는지 확인
    public boolean exists(Long userId) {
        return emitterMap.containsKey(userId);
    }

    // 전체 연결 초기화
    // 테스트나 서버 재시작 시 모든 Emitter를 초기화할 때 사용
    public void clear() {
        emitterMap.clear();
    }
}
