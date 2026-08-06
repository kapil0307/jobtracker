package com.kapil.jobtracker.notification.mapper;

import com.kapil.jobtracker.notification.dto.NotificationResponse;
import com.kapil.jobtracker.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationResponse toResponse(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getInterview().getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getScheduledFor(),
                notification.getSentAt(),
                notification.getReadAt(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }
}
