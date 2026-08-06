package com.kapil.jobtracker.notification.scheduler;

import com.kapil.jobtracker.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000)
    public void processNotifications(){
        notificationService.processDueNotifications();
    }
}
