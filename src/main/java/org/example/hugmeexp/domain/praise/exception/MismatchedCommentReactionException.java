package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class MismatchedCommentReactionException extends BaseCustomException {
    public MismatchedCommentReactionException() {
        super(HttpStatus.BAD_REQUEST,"반응이 해당 댓글에 속하지 않습니다.",400);
    }
}
