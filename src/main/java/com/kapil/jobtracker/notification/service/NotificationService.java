package com.kapil.jobtracker.notification.service;

import com.kapil.jobtracker.interview.entity.Interview;
import com.kapil.jobtracker.notification.dto.NotificationResponse;
import com.kapil.jobtracker.notification.entity.Notification;
import com.kapil.jobtracker.notification.entity.NotificationStatus;
import com.kapil.jobtracker.notification.entity.NotificationType;
import com.kapil.jobtracker.notification.exception.NotificationNotFoundException;
import com.kapil.jobtracker.notification.mapper.NotificationMapper;
import com.kapil.jobtracker.notification.repository.NotificationRepository;
import com.kapil.jobtracker.security.service.CurrentUserService;
import com.kapil.jobtracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final CurrentUserService currentUserService;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;

    @Transactional
    public void createOrUpdateInterviewReminder(Interview interview) {

        if(interview.getScheduledAt().isBefore(LocalDateTime.now())){
            return;
        }
        LocalDateTime reminderTime =
                interview.getScheduledAt().minusHours(24);

        Notification notification = notificationRepo
                .findByInterviewId(interview.getId())
                .orElseGet(Notification::new);

        notification.setUser(interview.getJobApplication().getOwner());
        notification.setInterview(interview);
        notification.setType(NotificationType.INTERVIEW_REMINDER);
        notification.setTitle("Upcoming Interview");
        notification.setMessage(
                "Your interview for "
                        + interview.getJobApplication().getJobTitle()
                        + " is scheduled at "
                        + interview.getScheduledAt()
        );

        notification.setScheduledFor(reminderTime);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setSentAt(null);

        notificationRepo.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications() {

        User currentUser = currentUserService.getCurrentUser();

        return notificationRepo
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId){
        User currentUser = currentUserService.getCurrentUser();
        Notification notification = notificationRepo.findByIdAndUserId(notificationId, currentUser.getId())
                .orElseThrow(()-> new NotificationNotFoundException("Notification not found"));

        notification.setReadAt(LocalDateTime.now());
        Notification updated = notificationRepo.save(notification);
        return notificationMapper.toResponse(updated);
    }

    @Transactional
    public void processDueNotifications(){
        LocalDateTime now = LocalDateTime.now();

        List<Notification> dueNotifications = notificationRepo.findByStatusAndScheduledForLessThanEqual(
                NotificationStatus.PENDING,
                now
        );

        for(Notification notification: dueNotifications){
            try{
                String toEmail = notification.getUser().getEmail();
                emailService.sendInterviewReminder(
                        toEmail,
                        notification.getTitle(),
                        notification.getMessage()
                );
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(now);
            }
            catch (Exception e){
                notification.setStatus(NotificationStatus.FAILED);
            }
        }

        notificationRepo.saveAll(dueNotifications);
    }

    @Transactional
    public void cancelInterviewReminder(Long interviewId){
        notificationRepo.findByInterviewId(interviewId)
                .ifPresent(notification -> {
                    notification.setStatus(NotificationStatus.CANCELLED);
                    notificationRepo.save(notification);
                });
    }
}