package org.example.hugmeexp.domain.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자의 접근 제어자를 protected로 설정, JPA에서 엔티티를 프록시로 만들 때 사용
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 모든 필드를 파라미터로 받는 생성자 생성, 생성자의 접근 제어자를 private로 만듦
@Builder
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "attendance_date"}))
// user_id와 attendance_date의 조합이 유일해야 함을 명시, 동시성: 중복된 출석 기록 방지, insert만 방지하고 싶으면 낙관적/분산 락은 이거 하면 없어도 된다 합니다!
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // JPA에서 관리하는 id, 이거 생성 안 해두면 나중에 jpa 에서 엔티티 저장/조회 시 에러 난다고 함

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // User와 Attendance는 1:N 관계, User <-> Attendance의 양방향 매핑, cascade 위함

    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    // 정적 팩토리 메서드
    public static Attendance of(User user, LocalDate attendanceDate) {
        return Attendance.builder()
                .user(user)
                .attendanceDate(attendanceDate)
                .build();
    }
}

