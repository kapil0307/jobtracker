package com.kapil.jobtracker.interview.service;

import com.kapil.jobtracker.interview.dto.InterviewCreateRequest;
import com.kapil.jobtracker.interview.dto.InterviewResponse;
import com.kapil.jobtracker.interview.dto.InterviewUpdateRequest;
import com.kapil.jobtracker.interview.entity.Interview;
import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.interview.entity.InterviewType;
import com.kapil.jobtracker.interview.exception.InterviewNotFoundException;
import com.kapil.jobtracker.interview.mapper.InterviewMapper;
import com.kapil.jobtracker.interview.repository.InterviewRepository;
import com.kapil.jobtracker.jobapplication.entity.JobApplication;
import com.kapil.jobtracker.jobapplication.exception.JobApplicationNotFoundException;
import com.kapil.jobtracker.jobapplication.repository.JobApplicationRepository;
import com.kapil.jobtracker.jobapplication.service.JobApplicationService;
import com.kapil.jobtracker.security.service.CurrentUserService;
import com.kapil.jobtracker.user.entity.User;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceTest {

    private User currentUser;
    private JobApplication jobApplication;
    private InterviewCreateRequest request;
    private Interview interview;
    private InterviewResponse expectedResponse;
    private InterviewUpdateRequest updateRequest;

    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private InterviewMapper interviewMapper;
    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private InterviewService interviewService;


    @BeforeEach
    void setUp() {

        currentUser = new User();
        currentUser.setId(1L);

        jobApplication = new JobApplication();
        jobApplication.setId(10L);
        jobApplication.setOwner(currentUser);

        request = new InterviewCreateRequest();
        request.setJobApplicationId(10L);
        request.setType(InterviewType.TECHNICAL);
        request.setStatus(InterviewStatus.SCHEDULED);
        request.setScheduledAt(LocalDateTime.now().plusDays(2));

        interview = new Interview();
        interview.setId(100L);

        updateRequest = new InterviewUpdateRequest();
        updateRequest.setType(InterviewType.TECHNICAL);
        updateRequest.setStatus(InterviewStatus.COMPLETED);
        updateRequest.setScheduledAt(LocalDateTime.now().minusDays(2));
        updateRequest.setMeetingLink("https://meet.google.com/example");
        updateRequest.setNotes("Interview completed");
        updateRequest.setFeedback("Good interview");
        updateRequest.setRating(4);

        expectedResponse = new InterviewResponse(
                100L,
                10L,
                InterviewType.TECHNICAL,
                InterviewStatus.SCHEDULED,
                request.getScheduledAt(),
                null,
                null,
                null,
                null
        );
    }

    @Test
    void shouldCreateInterviewSuccessfully() {

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(jobApplicationRepository.findByIdAndOwner(10L, currentUser))
                .thenReturn(Optional.of(jobApplication));

        when(interviewMapper.toEntity(request))
                .thenReturn(interview);

        when(interviewRepository.save(interview))
                .thenReturn(interview);

        when(interviewMapper.toResponse(interview))
                .thenReturn(expectedResponse);

        InterviewResponse response =
                interviewService.createInterview(request);

        assertEquals(100L, response.getId());
        assertEquals(jobApplication, interview.getJobApplication());

        verify(interviewRepository).save(interview);
    }

    @Test
    void shouldThrowExceptionWhenJobApplicationNotFound(){
        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);
        when(jobApplicationRepository.findByIdAndOwner(10L, currentUser))
                .thenReturn(Optional.empty());

        assertThrows(JobApplicationNotFoundException.class,
                ()-> interviewService.createInterview(request));

        verify(interviewRepository, never()).save(any());
    }

    @Test
    void shouldGetInterviewByIdSuccessfully(){
        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);
        when(interviewRepository.findByIdAndJobApplication_Owner(
                100L, currentUser
        )).thenReturn(Optional.of(interview));

        when(interviewMapper.toResponse(interview))
                .thenReturn(expectedResponse);

        InterviewResponse response = interviewService.getInterviewById(100L);
        assertEquals(100L, response.getId());
        verify(interviewMapper).toResponse(interview);
    }

    @Test
    void shouldThrowExceptionWhenInterviewNotFound() {

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(interviewRepository.findByIdAndJobApplication_Owner(
                100L,
                currentUser
        )).thenReturn(Optional.empty());

        assertThrows(
                InterviewNotFoundException.class,
                () -> interviewService.getInterviewById(100L)
        );

        verify(interviewMapper, never()).toResponse(any());
    }

    @Test
    void shouldGetAllInterviewsSuccessfully() {
        Pageable pageable = PageRequest.of(0, 5);

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        Page<Interview> interviewPage =
                new PageImpl<>(List.of(interview), pageable, 1);

        when(interviewRepository.findAllByJobApplication_Owner(
                currentUser,
                pageable
        )).thenReturn(interviewPage);

        when(interviewMapper.toResponse(interview))
                .thenReturn(expectedResponse);

        Page<InterviewResponse> responses =
                interviewService.getAllInterviews(pageable);

        assertEquals(1, responses.getTotalElements());
        assertEquals(1, responses.getContent().size());
        assertEquals(100L, responses.getContent().get(0).getId());

        verify(interviewRepository)
                .findAllByJobApplication_Owner(currentUser, pageable);
    }

    @Test
    void shouldUpdateInterviewSuccessfully() {
        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(interviewRepository.findByIdAndJobApplication_Owner(
                100L,
                currentUser
        )).thenReturn(Optional.of(interview));

        when(interviewRepository.save(interview))
                .thenReturn(interview);

        when(interviewMapper.toResponse(interview))
                .thenReturn(expectedResponse);

        InterviewResponse response =
                interviewService.updateInterview(100L, updateRequest);

        assertEquals(100L, response.getId());
        assertEquals(updateRequest.getType(), interview.getType());
        assertEquals(updateRequest.getStatus(), interview.getStatus());
        assertEquals(updateRequest.getScheduledAt(), interview.getScheduledAt());
        assertEquals(updateRequest.getFeedback(), interview.getFeedback());
        assertEquals(updateRequest.getRating(), interview.getRating());

        verify(interviewRepository).save(interview);
    }

    @Test
    void shouldDeleteInterviewSuccessfully() {

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(interviewRepository.findByIdAndJobApplication_Owner(
                100L,
                currentUser
        )).thenReturn(Optional.of(interview));

        interviewService.deleteInterview(100L);

        verify(interviewRepository).delete(interview);
    }

    @Test
    void shouldGetInterviewsByStatusSuccessfully() {

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(interviewRepository
                .findAllByJobApplication_OwnerAndStatusOrderByScheduledAtAsc(
                        currentUser,
                        InterviewStatus.SCHEDULED
                ))
                .thenReturn(List.of(interview));

        when(interviewMapper.toResponse(interview))
                .thenReturn(expectedResponse);

        List<InterviewResponse> responses =
                interviewService.getInterviewByStatus(
                        InterviewStatus.SCHEDULED
                );

        assertEquals(1, responses.size());
        assertEquals(100L, responses.get(0).getId());
    }
}
