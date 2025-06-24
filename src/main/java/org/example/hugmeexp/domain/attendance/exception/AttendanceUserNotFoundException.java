package org.example.hugmeexp.domain.attendance.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AttendanceUserNotFoundException extends BaseCustomException {
    private static final String MESSAGE = "출석체크 대상 사용자를 찾을 수 없습니다.";
    private static final int CODE = 404;

    public AttendanceUserNotFoundException() {
        super(HttpStatus.NOT_FOUND, MESSAGE, CODE);
    }
}
