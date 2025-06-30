package org.example.hugmeexp.domain.bookmark.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class BookmarkUserNotFoundException extends BaseCustomException{

    private static final String MESSAGE = "북마크 사용자를 찾을 수 없습니다.";
    private static final int CODE = 404;

    public BookmarkUserNotFoundException() {super(HttpStatus.NOT_FOUND, MESSAGE, CODE);}
}
