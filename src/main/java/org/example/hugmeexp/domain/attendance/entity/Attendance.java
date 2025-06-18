package org.example.hugmeexp.domain.attendance.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.global.entity.User;

import java.time.LocalDate;

@Getter
@Entity
@Builder
/*
@Builder(builderMethodName = "hiddenBuilder") 이렇게 하면 정적 팩토리 메서드로만 객체 생성이 가능하다고 합니다.
그냥 @Builder만 쓰면 누구나 Attendance.builder()로 객체를 만들 수 있어서,
정적 팩토리 메서드만 사용 하자는 팀 규칙에 hiddenBuilder가 더 적합할 수도 있을 것 같은데 일단 보류해놓겠습니다.
 */
@Table(name = "attendance")
@NoArgsConstructor(force = true) // final 필드가 있을 땐, force = true 로 final 필드도 0, null로 초기화 해주지 않으면 컴파일 에러가 난다고 합니다.
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // JPA에서 관리하는 id, 이거 생성 안 해두면 나중에 jpa 에서 엔티티 저장/조회 시 에러 난다고 함

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private final Long userName;
    private final LocalDate attendanceDate;
    private final int exp;
    private final int point;

    // 내부적으로만 쓰는 생성자, 정적 팩토리 메서드 of로 외부에서도 호출 가능한 생성자를 만들 수 있음
    private Attendance(Long id, Long userName, LocalDate attendanceDate, int exp, int point) {
        this.id = id; // JPA에서 관리하는 id
        this.userName = userName;
        this.attendanceDate = attendanceDate;
        this.exp = exp;
        this.point = point;
    }

    //정적 팩토리 메서드
    public static Attendance createForCheck(Long userId, LocalDate attendanceDate, int exp, int point) {
        return Attendance.builder()
                .userName(userId)
                .attendanceDate(attendanceDate)
                .exp(exp)
                .point(point)
                .build();
    }

    // 외부에서 호출 가능한 객체 생성 메서드
    public static Attendance of(Long userId, LocalDate attendanceDate, int exp, int point) {
        return Attendance.builder()
                .userName(userId)
                .attendanceDate(attendanceDate)
                .exp(exp)
                .point(point)
                .build();
    }


}

