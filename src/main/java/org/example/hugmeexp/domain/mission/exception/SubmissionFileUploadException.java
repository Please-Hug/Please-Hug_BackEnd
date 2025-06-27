package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class SubmissionFileUploadException extends BaseCustomException {
    public SubmissionFileUploadException() {
        super(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다. 파일 형식이 올바른지 확인해주세요.");
    }

    public SubmissionFileUploadException(String s) {
        super(HttpStatus.BAD_REQUEST, s);
    }
}
