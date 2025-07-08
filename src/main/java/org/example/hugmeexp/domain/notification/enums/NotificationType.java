package org.example.hugmeexp.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum NotificationType {

    PRAISE_RECEIVED("칭찬을 받았어요", Category.ACTIVITY),
    DIARY_COMMENT("내 배움일기에 댓글이 달렸어요", Category.ACTIVITY),
    DIARY_LIKE("내 배움일기에 좋아요가 달렸어요", Category.ACTIVITY),
    LEVEL_UP("레벨 업 했어요", Category.REWARD),;

    private final String description;
    private final Category category;

    public enum Category {
        ACTIVITY,
        REWARD
    }
}
