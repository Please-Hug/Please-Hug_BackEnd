package org.example.hugmeexp.domain.qeust.validator;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.attendance.repository.AttendanceRepository;
import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class AttendanceValidator implements QuestValidator {

    private final AttendanceRepository attendanceRepository;

    @Override
    public boolean isValid(UserQuest userQuest) {
        String username = userQuest.getUser().getUsername();
        LocalDate today = LocalDate.now();
        return !attendanceRepository.findByUser_UsernameAndAttendanceDateBetween(username, today, today).isEmpty();
    }
}