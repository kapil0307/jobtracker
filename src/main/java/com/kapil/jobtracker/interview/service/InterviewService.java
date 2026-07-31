package com.kapil.jobtracker.interview.service;

import com.kapil.jobtracker.interview.dto.InterviewCreateRequest;
import com.kapil.jobtracker.interview.dto.InterviewResponse;
import com.kapil.jobtracker.interview.dto.InterviewUpdateRequest;
import com.kapil.jobtracker.interview.entity.Interview;
import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.interview.exception.InterviewNotFoundException;
import com.kapil.jobtracker.interview.mapper.InterviewMapper;
import com.kapil.jobtracker.interview.repository.InterviewRepository;
import com.kapil.jobtracker.jobapplication.entity.JobApplication;
import com.kapil.jobtracker.jobapplication.exception.JobApplicationNotFoundException;
import com.kapil.jobtracker.jobapplication.repository.JobApplicationRepository;
import com.kapil.jobtracker.security.service.CurrentUserService;
import com.kapil.jobtracker.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepo;
    private final JobApplicationRepository jobApplicationRepo;
    private final InterviewMapper interviewMapper;
    private final CurrentUserService currentUserService;

    @Transactional
    public InterviewResponse createInterview(InterviewCreateRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        JobApplication jobApplication = jobApplicationRepo.findByIdAndOwner(request.getJobApplicationId(), currentUser)
                .orElseThrow(() -> new JobApplicationNotFoundException("Job application not found"));

        Interview interview = interviewMapper.toEntity(request);

        interview.setJobApplication(jobApplication);
        Interview savedInterview = interviewRepo.save(interview);

        return interviewMapper.toResponse(savedInterview);
    }

    public InterviewResponse getInterviewById(Long interviewId) {
        User currentUser = currentUserService.getCurrentUser();

        Interview interview = interviewRepo.findByIdAndJobApplication_Owner(interviewId, currentUser)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found"));
        return interviewMapper.toResponse(interview);
    }

    @Transactional(readOnly = true)
    public Page<InterviewResponse> getAllInterviews(Pageable pageable) {
        User currentUser = currentUserService.getCurrentUser();

        return interviewRepo.findAllByJobApplication_Owner(currentUser, pageable)
                .map(interviewMapper::toResponse);
    }

    @Transactional
    public InterviewResponse updateInterview(Long interviewId, InterviewUpdateRequest request){
        User currentUser = currentUserService.getCurrentUser();

        Interview interview = interviewRepo.findByIdAndJobApplication_Owner(interviewId, currentUser)
                .orElseThrow(()-> new InterviewNotFoundException("Interview not found"));

        interview.setType(request.getType());
        interview.setStatus(request.getStatus());
        interview.setScheduledAt(request.getScheduledAt());
        interview.setMeetingLink(request.getMeetingLink());
        interview.setNotes(request.getNotes());
        interview.setFeedback(request.getFeedback());
        interview.setRating(request.getRating());

        Interview updated = interviewRepo.save(interview);
        return interviewMapper.toResponse(updated);
    }

    public void  deleteInterview(Long interviewId){
        User currentUser = currentUserService.getCurrentUser();

        Interview interview = interviewRepo.findByIdAndJobApplication_Owner(interviewId, currentUser)
                .orElseThrow(()-> new InterviewNotFoundException("Interview not found"));
        interviewRepo.delete(interview);
    }

    public List<InterviewResponse> getInterviewByStatus(InterviewStatus status){
        User currentUser = currentUserService.getCurrentUser();
        return interviewRepo.findAllByJobApplication_OwnerAndStatusOrderByScheduledAtAsc(
                currentUser, status
        )
                .stream()
                .map(interviewMapper::toResponse)
                .toList();
    }

    public List<InterviewResponse>  getUpcomingInterviews(){
        User currentUser = currentUserService.getCurrentUser();
        return interviewRepo. findAllByJobApplication_OwnerAndStatusAndScheduledAtAfterOrderByScheduledAtAsc(
                currentUser,
                InterviewStatus.SCHEDULED,
                LocalDateTime.now()
        )
                .stream()
                .map(interviewMapper::toResponse)
                .toList();
    }
}