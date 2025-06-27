package org.example.hugmeexp.domain.user.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;
public class UnsupportedImageExtensionException extends BaseCustomException {
    public UnsupportedImageExtensionException(String ext) {
        super(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 확장자 : " + ext, 400);
    }
}

