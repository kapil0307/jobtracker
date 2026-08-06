package com.kapil.jobtracker.notification.controller;

import com.kapil.jobtracker.notification.dto.NotificationResponse;
import com.kapil.jobtracker.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(){
        return new ResponseEntity<>(notificationService.getNotifications(), HttpStatus.OK);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId){
        return new ResponseEntity<>(notificationService.markAsRead(notificationId), HttpStatus.OK);
    }
}
