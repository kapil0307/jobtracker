package com.kapil.jobtracker.notification.repository;

import com.kapil.jobtracker.notification.entity.Notification;
import com.kapil.jobtracker.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByInterviewId(Long interviewId);

    List<Notification> findByStatusAndScheduledForLessThanEqual(NotificationStatus status,
                                                                LocalDateTime scheduledFor);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Notification> findByInterviewId(Long interviewId);

    Optional<Notification> findByIdAndUserId(Long notificationId, Long userId);
}
