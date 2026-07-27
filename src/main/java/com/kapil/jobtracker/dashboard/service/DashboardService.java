package com.kapil.jobtracker.dashboard.service;

import com.kapil.jobtracker.company.repository.CompanyRepository;
import com.kapil.jobtracker.dashboard.dto.DashboardResponse;
import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.interview.repository.InterviewRepository;
import com.kapil.jobtracker.jobapplication.repository.JobApplicationRepository;
import com.kapil.jobtracker.security.service.CurrentUserService;
import com.kapil.jobtracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final JobApplicationRepository jobApplicationRepo;
    private final InterviewRepository interviewRepo;
    private final CurrentUserService currentUserService;
    private final CompanyRepository companyRepo;

    public DashboardResponse getDashboardStatistics(){
        User currentUser = currentUserService.getCurrentUser();

        long totalCompanies = companyRepo.countByOwner(currentUser);

        long totalApplications = jobApplicationRepo.countByOwner(currentUser);

        long scheduledInterviews = interviewRepo.countByJobApplication_OwnerAndStatus(
                currentUser,
                InterviewStatus.SCHEDULED
        );

        long completedInterviews = interviewRepo.countByJobApplication_OwnerAndStatus(
                currentUser,
                InterviewStatus.COMPLETED
        );

        long cancelledInterviews = interviewRepo.countByJobApplication_OwnerAndStatus(
                currentUser,
                InterviewStatus.CANCELLED
        );

        return new DashboardResponse(
                currentUser.getName(),
                totalCompanies,
                totalApplications,
                scheduledInterviews,
                completedInterviews,
                cancelledInterviews
        );
    }
}
