package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class PraiseEmojiReactionNotFoundException extends BaseCustomException {
    public PraiseEmojiReactionNotFoundException() {
        super(HttpStatus.NOT_FOUND,"칭찬 게시물에 반응이 없습니다.",404);
    }
}
