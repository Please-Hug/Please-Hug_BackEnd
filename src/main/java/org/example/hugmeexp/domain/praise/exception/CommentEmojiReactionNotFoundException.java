package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class CommentEmojiReactionNotFoundException extends BaseCustomException {
    public CommentEmojiReactionNotFoundException() {
        super(HttpStatus.NOT_FOUND,"댓글 반응을 찾을 수 없습니다",404);
    }
}
