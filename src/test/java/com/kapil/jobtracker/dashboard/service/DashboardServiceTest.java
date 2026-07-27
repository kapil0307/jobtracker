package com.kapil.jobtracker.dashboard.service;

import com.kapil.jobtracker.company.repository.CompanyRepository;
import com.kapil.jobtracker.dashboard.dto.DashboardResponse;
import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.interview.repository.InterviewRepository;
import com.kapil.jobtracker.jobapplication.repository.JobApplicationRepository;
import com.kapil.jobtracker.security.service.CurrentUserService;
import com.kapil.jobtracker.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnDashboardStatistics() {

        User currentUser = new User();
        currentUser.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(companyRepository.countByOwner(currentUser))
                .thenReturn(3L);

        when(jobApplicationRepository.countByOwner(currentUser))
                .thenReturn(8L);

        when(interviewRepository.countByJobApplication_OwnerAndStatus(
                currentUser,
                InterviewStatus.SCHEDULED
        )).thenReturn(3L);

        when(interviewRepository.countByJobApplication_OwnerAndStatus(
                currentUser,
                InterviewStatus.COMPLETED
        )).thenReturn(2L);

        when(interviewRepository.countByJobApplication_OwnerAndStatus(
                currentUser,
                InterviewStatus.CANCELLED
        )).thenReturn(1L);

        currentUser.setName("Kapil");
        DashboardResponse response =
                dashboardService.getDashboardStatistics();

        assertEquals("Kapil", response.getUserName());
        assertEquals(3L, response.getTotalCompanies());
        assertEquals(8L, response.getTotalApplication());
        assertEquals(3L, response.getScheduledInterviews());
        assertEquals(2L, response.getCompletedInterviews());
        assertEquals(1L, response.getCancelledInterviews());
    }
}