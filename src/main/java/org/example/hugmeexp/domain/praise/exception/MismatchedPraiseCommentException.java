package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class MismatchedPraiseCommentException extends BaseCustomException {
    public MismatchedPraiseCommentException() {
        super(HttpStatus.BAD_REQUEST,"댓글이 해당 칭찬에 속하지 않습니다.",400);
    }
}
