package org.example.hugmeexp.domain.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.global.entity.User;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(force = true) // final 필드가 있을 땐, force = true 로 final 필드도 0, null로 초기화 해주지 않으면 컴파일 에러가 난다고 합니다.
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 모든 필드를 파라미터로 바든ㄴ 생성자 생성, 생성자의 접근 제어자를 private로 만듦
@Builder
/*
@Builder(builderMethodName = "hiddenBuilder") 이렇게 하면 정적 팩토리 메서드로만 객체 생성이 가능하다고 합니다.
그냥 @Builder만 쓰면 누구나 Attendance.builder()로 객체를 만들 수 있어서,
정적 팩토리 메서드만 사용 하자는 팀 규칙에 hiddenBuilder가 더 적합할 수도 있을 것 같은데 일단 보류해놓겠습니다.
 */
@Table(name = "attendance")
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // JPA에서 관리하는 id, 이거 생성 안 해두면 나중에 jpa 에서 엔티티 저장/조회 시 에러 난다고 함

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private final Long userName;

    @Column(nullable = false)
    private final LocalDate attendanceDate;

    @Column(nullable = false)
    private final int exp;

    @Column(nullable = false)
    private final int point;

    // 정적 팩토리 메서드
    public static Attendance of(User user, LocalDate attendanceDate, int exp, int point) {
        return Attendance.builder()
                .user(user)
                .attendanceDate(attendanceDate)
                .exp(exp)
                .point(point)
                .build();
    }
}

