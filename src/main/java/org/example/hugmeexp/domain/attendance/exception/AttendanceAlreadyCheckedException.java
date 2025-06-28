package org.example.hugmeexp.domain.attendance.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AttendanceAlreadyCheckedException extends BaseCustomException {
    private static final String MESSAGE = "이미 출석체크가 완료되었습니다.";
    private static final int CODE = 409;

    public AttendanceAlreadyCheckedException() {
        super(HttpStatus.CONFLICT, MESSAGE, CODE);
    }
}
