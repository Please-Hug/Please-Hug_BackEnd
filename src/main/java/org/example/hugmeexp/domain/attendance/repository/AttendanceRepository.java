package org.example.hugmeexp.domain.attendance.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.hugmeexp.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 주간/범위 조회
    List<Attendance> findByUserIdAndAttendanceDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // 특정 날짜 조회
    boolean existsByUserIdAndAttendanceDate(Long userId, LocalDate attendanceDate);



    // 한 유저가 지금까지 출석한 날짜들만 리스트 조회
    @Query("SELECT a.attendanceDate FROM Attendance a WHERE a.user.userId = :userId ORDER BY a.attendanceDate ASC")
    List<LocalDate> findDatesByUserId(@Param("userId") Long userId);
}