package com.kapil.jobtracker.notification.dto;

import com.kapil.jobtracker.notification.entity.NotificationStatus;
import com.kapil.jobtracker.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long interviewId;
    private NotificationType type;
    private String title;
    private String message;
    private LocalDateTime scheduledFor;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}
