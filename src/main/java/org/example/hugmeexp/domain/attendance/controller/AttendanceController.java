package org.example.hugmeexp.domain.attendance.controller;

import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckRequest;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public AttendanceStatusResponse getAttendanceStatus() {
        return attendacneService.getAttendanceStatus();
    }

    @PostMapping("/check")
    public AttendanceCheckResponse checkAttendance(@RequestBody AttendanceCheckRequest request) {
        return attendacneService.checkAttendance(request);
    }
}
