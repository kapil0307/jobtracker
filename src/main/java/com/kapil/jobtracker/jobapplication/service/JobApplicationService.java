package com.kapil.jobtracker.jobapplication.service;

import com.kapil.jobtracker.company.entity.Company;
import com.kapil.jobtracker.company.exception.CompanyNotFoundException;
import com.kapil.jobtracker.company.repository.CompanyRepository;
import com.kapil.jobtracker.jobapplication.dto.JobApplicationRequest;
import com.kapil.jobtracker.jobapplication.dto.JobApplicationResponse;
import com.kapil.jobtracker.jobapplication.entity.ApplicationStatus;
import com.kapil.jobtracker.jobapplication.entity.JobApplication;
import com.kapil.jobtracker.jobapplication.exception.DuplicateJobApplicationException;
import com.kapil.jobtracker.jobapplication.exception.InvalidApplicationStateException;
import com.kapil.jobtracker.jobapplication.exception.JobApplicationNotFoundException;
import com.kapil.jobtracker.jobapplication.mapper.JobApplicationMapper;
import com.kapil.jobtracker.jobapplication.repository.JobApplicationRepository;
import com.kapil.jobtracker.user.entity.User;
import com.kapil.jobtracker.user.exception.UserNotFoundException;
import com.kapil.jobtracker.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class JobApplicationService {
    private final JobApplicationRepository jobApplicationRepo;
    private final JobApplicationMapper jobApplicationMapper;
    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;

    @Transactional
    public JobApplicationResponse createJobApplication(Long userId, JobApplicationRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User with id " + userId + " not found"));

        Company company= companyRepo.findByIdAndOwnerId(request.getCompanyId(), userId)
                .orElseThrow(()-> new CompanyNotFoundException("Company with id " + request.getCompanyId() +" not found for user " + userId));

        if(request.getStatus() != ApplicationStatus.SAVED
                && request.getAppliedDate() == null){
            throw new InvalidApplicationStateException(
                    "Applied date is required when application status is not SAVED"
            );
        }
        JobApplication jobApplication = jobApplicationMapper.toJobApplication(request);

        jobApplication.setOwner(user);
        jobApplication.setCompany(company);

        boolean alreadyExists = jobApplicationRepo.existsByOwnerAndCompanyAndJobTitleIgnoreCase(
                user,
                company,
                request.getJobTitle()
        );
        if(alreadyExists){
            throw new DuplicateJobApplicationException("You have already applied for this position in this company");
        }

        JobApplication jobApplicationSaved = jobApplicationRepo.save(jobApplication);

        return jobApplicationMapper.toResponse(jobApplicationSaved);
    }

    @Transactional(readOnly = true)
    public JobApplicationResponse getApplicationById(Long userId, Long applicationId) {
        JobApplication application = jobApplicationRepo.findByIdAndOwnerId(applicationId, userId)
                .orElseThrow(()-> new JobApplicationNotFoundException("Job application with id " + applicationId + "  not found"));

        return  jobApplicationMapper.toResponse(application);
    }

    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getAllApplicationByUserId(Long userId, Pageable pageable) {
        return jobApplicationRepo.findAllByOwnerId(userId, pageable)
                .map(jobApplicationMapper::toResponse);
    }

    @Transactional
    public JobApplicationResponse updateJobApplication(Long userId, Long applicationId, JobApplicationRequest request) {
        JobApplication application = jobApplicationRepo.findByIdAndOwnerId(applicationId, userId)
                .orElseThrow(()-> new JobApplicationNotFoundException("Job application with id " + applicationId + " not found"));

        Company company = companyRepo.findByIdAndOwnerId(request.getCompanyId(), userId)
                .orElseThrow(()-> new CompanyNotFoundException("Company with id " + request.getCompanyId() + " not found"));

        if(request.getStatus() != ApplicationStatus.SAVED
                && request.getAppliedDate() == null){
            throw new InvalidApplicationStateException(
                    "Applied date is required when application status is not SAVED"
            );
        }

        application.setJobTitle(request.getJobTitle());
        application.setStatus(request.getStatus());
        application.setAppliedDate(request.getAppliedDate());
        application.setJobUrl(request.getJobUrl());
        application.setJobLocation(request.getJobLocation());
        application.setSalaryRange(request.getSalaryRange());
        application.setNotes(request.getNotes());
        application.setSource(request.getSource());
        application.setCompany(company);

        return jobApplicationMapper.toResponse(application);
    }

    @Transactional
    public void deleteJobApplication(Long userId, Long applicationId) {
        JobApplication application = jobApplicationRepo.findByIdAndOwnerId(applicationId, userId)
                .orElseThrow(()-> new JobApplicationNotFoundException("Job application with id " + applicationId + " not found"));

        jobApplicationRepo.delete(application);
    }
}
