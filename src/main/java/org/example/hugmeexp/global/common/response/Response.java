package org.example.hugmeexp.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 값은 JSON에 포함하지 않음
public class Response<T> {
    private final String message;
    private final T data;
}
