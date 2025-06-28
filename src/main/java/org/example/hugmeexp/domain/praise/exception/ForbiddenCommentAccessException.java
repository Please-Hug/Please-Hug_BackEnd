package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ForbiddenCommentAccessException extends BaseCustomException {
    public ForbiddenCommentAccessException() {
        super(HttpStatus.FORBIDDEN,"댓글 삭제 권한이 없습니다",403);
    }
}
