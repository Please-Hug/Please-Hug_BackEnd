package org.example.hugmeexp.domain.attendance.policy;

import org.springframework.stereotype.Component;

@Component
public class RewardPolicy {
    public int getExp() {
        return 30; // 출첵 경험치
    }
    public int getPoint() {
        return 1; // 출첵 구름조각
    }
}
