package org.example.hugmeexp.domain.bookmark.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class BookmarkNotFoundException extends BaseCustomException {

    private static final String MESSAGE = "북마크를 찾을 수 없습니다.";
    private static final int CODE = 404;

    public BookmarkNotFoundException() {super(HttpStatus.NOT_FOUND, MESSAGE, CODE);}
}