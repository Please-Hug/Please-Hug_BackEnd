package org.example.hugmeexp.domain.praise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum PraiseType {

    THANKS("감사해요"),
    CHEER("응원해요"),
    RECOGNIZE("인정해요");

    private final String label;

}
