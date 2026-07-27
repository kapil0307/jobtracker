package com.kapil.jobtracker.jobapplication.mapper;


import com.kapil.jobtracker.jobapplication.dto.JobApplicationRequest;
import com.kapil.jobtracker.jobapplication.dto.JobApplicationResponse;
import com.kapil.jobtracker.jobapplication.entity.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {
    public JobApplication toJobApplication(JobApplicationRequest request) {
        JobApplication job = new JobApplication();

        job.setJobTitle(request.getJobTitle());
        job.setStatus(request.getStatus());
        job.setAppliedDate(request.getAppliedDate());
        job.setJobUrl(request.getJobUrl());
        job.setJobLocation(request.getJobLocation());
        job.setSalaryRange(request.getSalaryRange());
        job.setNotes(request.getNotes());
        job.setSource(request.getSource());

        return job;
    }

    public JobApplicationResponse toResponse(JobApplication job) {
        JobApplicationResponse response = new JobApplicationResponse();

        response.setId(job.getId());
        response.setJobTitle(job.getJobTitle());
        response.setStatus(job.getStatus());
        response.setAppliedDate(job.getAppliedDate());
        response.setJobUrl(job.getJobUrl());
        response.setJobLocation(job.getJobLocation());
        response.setSalaryRange(job.getSalaryRange());
        response.setNotes(job.getNotes());

        response.setCompanyId(job.getCompany().getId());
        response.setCompanyName(job.getCompany().getName());

        response.setOwnerId(job.getOwner().getId());
        response.setOwnerName(job.getOwner().getName());

        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());
        response.setSource(job.getSource());

        return  response;
    }
}
