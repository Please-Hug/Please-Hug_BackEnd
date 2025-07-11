package org.example.hugmeexp.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.notification.entity.Notification;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDeleteDTO {

    private Long notificationId;

    public static NotificationDeleteDTO from(Notification notification) {
        return NotificationDeleteDTO.builder()
                .notificationId(notification.getId())
                .build();
    }
}
