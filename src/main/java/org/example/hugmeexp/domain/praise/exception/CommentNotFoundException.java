package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends BaseCustomException {
    public CommentNotFoundException() {
        super(HttpStatus.NOT_FOUND,"댓글을 찾을 수 없습니다",404);
    }
}
