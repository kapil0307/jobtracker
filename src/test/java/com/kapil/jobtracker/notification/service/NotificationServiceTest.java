package com.kapil.jobtracker.notification.service;

import com.kapil.jobtracker.interview.entity.Interview;
import com.kapil.jobtracker.jobapplication.entity.JobApplication;
import com.kapil.jobtracker.notification.dto.NotificationResponse;
import com.kapil.jobtracker.notification.entity.Notification;
import com.kapil.jobtracker.notification.entity.NotificationStatus;
import com.kapil.jobtracker.notification.entity.NotificationType;
import com.kapil.jobtracker.notification.exception.NotificationNotFoundException;
import com.kapil.jobtracker.notification.mapper.NotificationMapper;
import com.kapil.jobtracker.notification.repository.NotificationRepository;
import com.kapil.jobtracker.security.service.CurrentUserService;
import com.kapil.jobtracker.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepo;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private NotificationMapper notificationMapper;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private User currentUser;
    private JobApplication jobApplication;
    private Interview  interview;
    private Notification notification;
    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp(){
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@example.com");

        jobApplication = new JobApplication();
        jobApplication.setId(10L);
        jobApplication.setJobTitle("Java Backend Developer");
        jobApplication.setOwner(currentUser);

        interview=new Interview();
        interview.setId(100L);
        interview.setJobApplication(jobApplication);
        interview.setScheduledAt(LocalDateTime.of(2026, 8,9,15,0)
        );

        notification = Notification.builder()
                .id(20L)
                .user(currentUser)
                .interview(interview)
                .type(NotificationType.INTERVIEW_REMINDER)
                .title("Upcoming Interview")
                .message("Interview remainder")
                .scheduledFor(LocalDateTime.of(2026,8,9,15,0))
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        notificationResponse = new NotificationResponse(
                200L,
                100L,
                NotificationType.INTERVIEW_REMINDER,
                "Upcoming Interview",
                "Interview reminder",
                LocalDateTime.of(2026,8,9,15,0),
                null,
                null,
                NotificationStatus.PENDING,
                notification.getCreatedAt()
        );
    }

    @Test
    void shouldCreateNewInterviewReminder(){
        when(notificationRepo.findByInterviewId(100L))
                .thenReturn(Optional.empty());
        notificationService.createOrUpdateInterviewReminder(interview);
        ArgumentCaptor<Notification> captor =ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepo).save(captor.capture());
        Notification savedNotification = captor.getValue();

        assertEquals(NotificationType.INTERVIEW_REMINDER, savedNotification.getType());
        assertEquals(NotificationStatus.PENDING, savedNotification.getStatus());
        assertEquals(interview.getScheduledAt().minusHours(24), savedNotification.getScheduledFor());
        assertNull(savedNotification.getSentAt());
        assertEquals("Upcoming Interview", savedNotification.getTitle());
        assertTrue(savedNotification.getMessage().contains("Java Backend Developer"));
    }

    @Test
    void shouldUpdateExistingInterviewReminder(){
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());

        when(notificationRepo.findByInterviewId(100L))
                .thenReturn(Optional.of(notification));

        LocalDateTime updatedInterviewTime=LocalDateTime.of(2026,8,15,12,0);
        interview.setScheduledAt(updatedInterviewTime);
        notificationService.createOrUpdateInterviewReminder(interview);
        verify(notificationRepo).save(notification);
        assertEquals(updatedInterviewTime.minusHours(24), notification.getScheduledFor());
        assertEquals(NotificationStatus.PENDING, notification.getStatus());
        assertNull(notification.getSentAt());
    }

    @Test
    void shouldGetNotificationsForCurrentUser() {

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(notificationRepo
                .findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(notificationResponse);

        List<NotificationResponse> responses =
                notificationService.getNotifications();

        assertEquals(1, responses.size());
        assertEquals(200L, responses.get(0).getId());
        assertEquals(100L, responses.get(0).getInterviewId());

        verify(notificationRepo)
                .findByUserIdOrderByCreatedAtDesc(1L);

        verify(notificationMapper)
                .toResponse(notification);
    }

    @Test
    void shouldMarkNotificationAsRead() {

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(notificationRepo.findByIdAndUserId(200L, 1L))
                .thenReturn(Optional.of(notification));

        when(notificationRepo.save(notification))
                .thenReturn(notification);

        when(notificationMapper.toResponse(notification))
                .thenReturn(notificationResponse);

        NotificationResponse response =
                notificationService.markAsRead(200L);

        assertNotNull(notification.getReadAt());
        assertEquals(notificationResponse, response);

        verify(notificationRepo)
                .findByIdAndUserId(200L, 1L);

        verify(notificationRepo)
                .save(notification);

        verify(notificationMapper)
                .toResponse(notification);
    }

    @Test
    void shouldThrowExceptionWhenNotificationNotFound() {

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(notificationRepo.findByIdAndUserId(200L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotificationNotFoundException.class,
                () -> notificationService.markAsRead(200L)
        );

        verify(notificationRepo, never())
                .save(any(Notification.class));

        verify(notificationMapper, never())
                .toResponse(any(Notification.class));
    }

    @Test
    void shouldProcessDueNotifications() {

        Notification secondNotification = Notification.builder()
                .id(201L)
                .user(currentUser)
                .interview(interview)
                .type(NotificationType.INTERVIEW_REMINDER)
                .title("Upcoming Interview")
                .message("Second reminder")
                .scheduledFor(LocalDateTime.now().minusMinutes(5))
                .status(NotificationStatus.PENDING)
                .build();

        when(notificationRepo
                .findByStatusAndScheduledForLessThanEqual(
                        eq(NotificationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of(notification, secondNotification));

        notificationService.processDueNotifications();

        assertEquals(
                NotificationStatus.SENT,
                notification.getStatus()
        );

        assertEquals(
                NotificationStatus.SENT,
                secondNotification.getStatus()
        );

        assertNotNull(notification.getSentAt());
        assertNotNull(secondNotification.getSentAt());

        verify(notificationRepo)
                .saveAll(List.of(notification, secondNotification));
        verify(emailService).sendInterviewReminder(
                currentUser.getEmail(),
                notification.getTitle(),
                notification.getMessage()
        );
    }

    @Test
    void shouldHandleWhenNoDueNotificationsExist() {

        when(notificationRepo
                .findByStatusAndScheduledForLessThanEqual(
                        eq(NotificationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of());

        notificationService.processDueNotifications();

        verify(notificationRepo)
                .saveAll(List.of());
    }

    @Test
    void shouldCancelInterviewReminder() {

        when(notificationRepo.findByInterviewId(100L))
                .thenReturn(Optional.of(notification));

        notificationService.cancelInterviewReminder(100L);

        assertEquals(
                NotificationStatus.CANCELLED,
                notification.getStatus()
        );

        verify(notificationRepo).save(notification);
    }

    @Test
    void shouldDoNothingWhenReminderNotFound() {

        when(notificationRepo.findByInterviewId(100L))
                .thenReturn(Optional.empty());

        notificationService.cancelInterviewReminder(100L);

        verify(notificationRepo, never())
                .save(any(Notification.class));
    }

    @Test
    void shouldNotCreateReminderForPastInterview() {

        interview.setScheduledAt(LocalDateTime.now().minusHours(1));

        notificationService.createOrUpdateInterviewReminder(interview);

        verify(notificationRepo, never())
                .findByInterviewId(anyLong());

        verify(notificationRepo, never())
                .save(any(Notification.class));
    }

    @Test
    void shouldMarkNotificationFailedWhenEmailSendingFails() {

        currentUser.setEmail("test@example.com");

        when(notificationRepo
                .findByStatusAndScheduledForLessThanEqual(
                        eq(NotificationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of(notification));

        doThrow(new RuntimeException("Mail failed"))
                .when(emailService)
                .sendInterviewReminder(
                        anyString(),
                        anyString(),
                        anyString()
                );

        notificationService.processDueNotifications();

        assertEquals(
                NotificationStatus.FAILED,
                notification.getStatus()
        );

        assertNull(notification.getSentAt());

        verify(notificationRepo)
                .saveAll(List.of(notification));
    }
}
