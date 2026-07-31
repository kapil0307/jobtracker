package com.kapil.jobtracker.jobApplication.service;

import com.kapil.jobtracker.company.entity.Company;
import com.kapil.jobtracker.company.exception.CompanyNotFoundException;
import com.kapil.jobtracker.company.repository.CompanyRepository;
import com.kapil.jobtracker.jobapplication.dto.JobApplicationRequest;
import com.kapil.jobtracker.jobapplication.dto.JobApplicationResponse;
import com.kapil.jobtracker.jobapplication.entity.ApplicationSource;
import com.kapil.jobtracker.jobapplication.entity.ApplicationStatus;
import com.kapil.jobtracker.jobapplication.entity.JobApplication;
import com.kapil.jobtracker.jobapplication.exception.DuplicateJobApplicationException;
import com.kapil.jobtracker.jobapplication.exception.InvalidApplicationStateException;
import com.kapil.jobtracker.jobapplication.exception.JobApplicationNotFoundException;
import com.kapil.jobtracker.jobapplication.mapper.JobApplicationMapper;
import com.kapil.jobtracker.jobapplication.repository.JobApplicationRepository;
import com.kapil.jobtracker.jobapplication.service.JobApplicationService;
import com.kapil.jobtracker.user.entity.User;
import com.kapil.jobtracker.user.exception.UserNotFoundException;
import com.kapil.jobtracker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepo;

    @Mock
    private JobApplicationMapper jobApplicationMapper;

    @Mock
    private UserRepository userRepo;

    @Mock
    private CompanyRepository companyRepo;

    @InjectMocks
    private JobApplicationService jobApplicationService;

    private User user;
    private Company company;
    private JobApplication application;
    private JobApplicationRequest request;
    private JobApplicationResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Kapil");

        company = new Company();
        company.setId(10L);
        company.setName("Google");
        company.setOwner(user);

        request = new JobApplicationRequest(
                "Java Backend Developer",
                ApplicationStatus.APPLIED,
                LocalDate.now(),
                "https://careers.google.com/job/123",
                "Bengaluru",
                "12-18 LPA",
                "Applied through careers page",
                10L,
                ApplicationSource.COMPANY_WEBSITE
        );

        application = new JobApplication();
        application.setId(100L);
        application.setJobTitle("Java Backend Developer");
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedDate(LocalDate.now());
        application.setJobUrl("https://careers.google.com/job/123");
        application.setJobLocation("Bengaluru");
        application.setSalaryRange("12-18 LPA");
        application.setNotes("Applied through careers page");
        application.setSource(ApplicationSource.COMPANY_WEBSITE);
        application.setOwner(user);
        application.setCompany(company);

        response = new JobApplicationResponse();
        response.setId(100L);
        response.setJobTitle("Java Backend Developer");
        response.setStatus(ApplicationStatus.APPLIED);
        response.setAppliedDate(LocalDate.now());
        response.setCompanyId(10L);
        response.setCompanyName("Google");
        response.setOwnerId(1L);
        response.setOwnerName("Kapil");
        response.setSource(ApplicationSource.COMPANY_WEBSITE);
    }

    @Test
    void createJobApplication_shouldCreateSuccessfully() {
        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.of(company));

        when(jobApplicationRepo.existsByOwnerAndCompanyAndJobTitleIgnoreCase(
                user,
                company,
                "Java Backend Developer"
        )).thenReturn(false);

        when(jobApplicationMapper.toJobApplication(request))
                .thenReturn(application);

        when(jobApplicationRepo.save(application))
                .thenReturn(application);

        when(jobApplicationMapper.toResponse(application))
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.createJobApplication(1L, request);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Java Backend Developer", result.getJobTitle());

        assertEquals(user, application.getOwner());
        assertEquals(company, application.getCompany());

        verify(userRepo).findById(1L);
        verify(companyRepo).findByIdAndOwnerId(10L, 1L);
        verify(jobApplicationRepo).save(application);
        verify(jobApplicationMapper).toResponse(application);
    }

    @Test
    void createJobApplication_shouldThrowWhenUserNotFound() {
        when(userRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> jobApplicationService.createJobApplication(1L, request)
        );

        verify(userRepo).findById(1L);
        verifyNoInteractions(companyRepo);
        verify(jobApplicationRepo, never()).save(any());
    }

    @Test
    void createJobApplication_shouldThrowWhenCompanyNotFound() {
        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> jobApplicationService.createJobApplication(1L, request)
        );

        verify(companyRepo).findByIdAndOwnerId(10L, 1L);
        verify(jobApplicationRepo, never()).save(any());
    }

    @Test
    void createJobApplication_shouldThrowWhenAppliedDateIsMissing() {
        request.setStatus(ApplicationStatus.APPLIED);
        request.setAppliedDate(null);

        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.of(company));

        assertThrows(
                InvalidApplicationStateException.class,
                () -> jobApplicationService.createJobApplication(1L, request)
        );

        verify(jobApplicationRepo, never()).save(any());
    }

    @Test
    void createJobApplication_shouldAllowNullAppliedDateForSavedStatus() {
        request.setStatus(ApplicationStatus.SAVED);
        request.setAppliedDate(null);

        application.setStatus(ApplicationStatus.SAVED);
        application.setAppliedDate(null);

        response.setStatus(ApplicationStatus.SAVED);
        response.setAppliedDate(null);

        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.of(company));

        when(jobApplicationRepo.existsByOwnerAndCompanyAndJobTitleIgnoreCase(
                user,
                company,
                request.getJobTitle()
        )).thenReturn(false);

        when(jobApplicationMapper.toJobApplication(request))
                .thenReturn(application);

        when(jobApplicationRepo.save(application))
                .thenReturn(application);

        when(jobApplicationMapper.toResponse(application))
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.createJobApplication(1L, request);

        assertNotNull(result);
        assertEquals(ApplicationStatus.SAVED, result.getStatus());
        assertNull(result.getAppliedDate());

        verify(jobApplicationRepo).save(application);
    }

    @Test
    void createJobApplication_shouldThrowDuplicateException() {
        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.of(company));

        when(jobApplicationMapper.toJobApplication(request))
                .thenReturn(application);

        when(jobApplicationRepo.existsByOwnerAndCompanyAndJobTitleIgnoreCase(
                user,
                company,
                request.getJobTitle()
        )).thenReturn(true);

        assertThrows(
                DuplicateJobApplicationException.class,
                () -> jobApplicationService.createJobApplication(1L, request)
        );

        verify(jobApplicationRepo, never()).save(any());
    }

    @Test
    void getApplicationById_shouldReturnApplicationSuccessfully() {
        when(jobApplicationRepo.findByIdAndOwnerId(100L, 1L))
                .thenReturn(Optional.of(application));

        when(jobApplicationMapper.toResponse(application))
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.getApplicationById(1L, 100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Java Backend Developer", result.getJobTitle());

        verify(jobApplicationRepo).findByIdAndOwnerId(100L, 1L);
        verify(jobApplicationMapper).toResponse(application);
    }

    @Test
    void getApplicationById_shouldThrowWhenApplicationNotFound() {
        when(jobApplicationRepo.findByIdAndOwnerId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                JobApplicationNotFoundException.class,
                () -> jobApplicationService.getApplicationById(1L, 100L)
        );

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void getAllApplicationByUserId_shouldReturnPaginatedApplications() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<JobApplication> applicationPage =
                new PageImpl<>(List.of(application), pageable, 1);

        when(jobApplicationRepo.findAllByOwnerId(1L, pageable))
                .thenReturn(applicationPage);

        when(jobApplicationMapper.toResponse(application))
                .thenReturn(response);

        Page<JobApplicationResponse> result =
                jobApplicationService.getAllApplicationByUserId(
                        1L,
                        pageable
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(100L, result.getContent().get(0).getId());

        verify(jobApplicationRepo).findAllByOwnerId(1L, pageable);
        verify(jobApplicationMapper).toResponse(application);
    }

    @Test
    void updateJobApplication_shouldUpdateSuccessfully() {
        JobApplicationRequest updateRequest = new JobApplicationRequest(
                "Senior Java Developer",
                ApplicationStatus.INTERVIEW,
                LocalDate.now(),
                "https://careers.google.com/job/updated",
                "Hyderabad",
                "18-25 LPA",
                "Interview scheduled",
                10L,
                ApplicationSource.COMPANY_WEBSITE
        );

        JobApplicationResponse updatedResponse = new JobApplicationResponse();
        updatedResponse.setId(100L);
        updatedResponse.setJobTitle("Senior Java Developer");
        updatedResponse.setStatus(ApplicationStatus.INTERVIEW);

        when(jobApplicationRepo.findByIdAndOwnerId(100L, 1L))
                .thenReturn(Optional.of(application));

        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.of(company));

        when(jobApplicationMapper.toResponse(application))
                .thenReturn(updatedResponse);

        JobApplicationResponse result =
                jobApplicationService.updateJobApplication(
                        1L,
                        100L,
                        updateRequest
                );

        assertNotNull(result);
        assertEquals("Senior Java Developer", application.getJobTitle());
        assertEquals(ApplicationStatus.INTERVIEW, application.getStatus());
        assertEquals("Hyderabad", application.getJobLocation());
        assertEquals("18-25 LPA", application.getSalaryRange());
        assertEquals(company, application.getCompany());

        /*
         update method mein save() explicitly call nahi ho raha.
         @Transactional ke through JPA dirty checking update karega.
        */
        verify(jobApplicationRepo, never()).save(any());
        verify(jobApplicationMapper).toResponse(application);
    }

    @Test
    void updateJobApplication_shouldThrowWhenApplicationNotFound() {
        when(jobApplicationRepo.findByIdAndOwnerId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                JobApplicationNotFoundException.class,
                () -> jobApplicationService.updateJobApplication(
                        1L,
                        100L,
                        request
                )
        );

        verifyNoInteractions(companyRepo);
        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void updateJobApplication_shouldThrowWhenCompanyNotFound() {
        when(jobApplicationRepo.findByIdAndOwnerId(100L, 1L))
                .thenReturn(Optional.of(application));

        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> jobApplicationService.updateJobApplication(
                        1L,
                        100L,
                        request
                )
        );

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void deleteJobApplication_shouldDeleteSuccessfully() {
        when(jobApplicationRepo.findByIdAndOwnerId(100L, 1L))
                .thenReturn(Optional.of(application));

        jobApplicationService.deleteJobApplication(1L, 100L);

        verify(jobApplicationRepo).findByIdAndOwnerId(100L, 1L);
        verify(jobApplicationRepo).delete(application);
    }

    @Test
    void deleteJobApplication_shouldThrowWhenApplicationNotFound() {
        when(jobApplicationRepo.findByIdAndOwnerId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                JobApplicationNotFoundException.class,
                () -> jobApplicationService.deleteJobApplication(1L, 100L)
        );

        verify(jobApplicationRepo, never()).delete(any());
    }
}