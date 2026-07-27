package com.kapil.jobtracker.jobapplication.controller;

import com.kapil.jobtracker.jobapplication.dto.JobApplicationRequest;
import com.kapil.jobtracker.jobapplication.dto.JobApplicationResponse;
import com.kapil.jobtracker.jobapplication.service.JobApplicationService;
import com.kapil.jobtracker.security.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<JobApplicationResponse> createJobApplication(@Valid @RequestBody JobApplicationRequest request) {
        Long userId = currentUserService.getCurrentUser().getId();

        JobApplicationResponse response= jobApplicationService.createJobApplication(userId, request);
        return new  ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAllJobApplications() {
        Long userId = currentUserService.getCurrentUser().getId();

        List<JobApplicationResponse> responses=jobApplicationService.getAllApplicationByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<JobApplicationResponse> getJobApplication(@PathVariable Long applicationId) {
        Long userId = currentUserService.getCurrentUser().getId();

        JobApplicationResponse response=jobApplicationService.getApplicationById(userId, applicationId);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/{applicationId}")
    public ResponseEntity<JobApplicationResponse> updateJobApplication(@PathVariable Long applicationId,
                                                                       @Valid @RequestBody JobApplicationRequest request) {
        Long userId = currentUserService.getCurrentUser().getId();

        JobApplicationResponse response=jobApplicationService.updateJobApplication(userId, applicationId, request);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> deleteJobApplication(@PathVariable Long applicationId) {
        Long userId = currentUserService.getCurrentUser().getId();

        jobApplicationService.deleteJobApplication(userId, applicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
