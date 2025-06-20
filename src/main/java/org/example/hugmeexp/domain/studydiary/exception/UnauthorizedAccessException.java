package org.example.hugmeexp.domain.studydiary.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends BaseCustomException {
    public UnauthorizedAccessException() {
        super(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", 403);
    }
} 