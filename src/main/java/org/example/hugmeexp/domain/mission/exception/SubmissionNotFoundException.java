package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class SubmissionNotFoundException extends BaseCustomException {
    public SubmissionNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
    public SubmissionNotFoundException() {
        super(HttpStatus.NOT_FOUND, "제출 정보를 찾을 수 없습니다.");
    }
}
