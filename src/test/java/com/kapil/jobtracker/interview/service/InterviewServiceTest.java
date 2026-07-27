package com.kapil.jobtracker.interview.service;

import com.kapil.jobtracker.interview.dto.InterviewRequest;
import com.kapil.jobtracker.interview.dto.InterviewResponse;
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
    private InterviewRequest request;
    private Interview interview;
    private InterviewResponse expectedResponse;

    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private JobApplicationService jobApplicationService;
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

        request = new InterviewRequest();
        request.setJobApplicationId(10L);
        request.setType(InterviewType.TECHNICAL);
        request.setStatus(InterviewStatus.SCHEDULED);
        request.setScheduledAt(LocalDateTime.now().plusDays(2));

        interview = new Interview();
        interview.setId(100L);

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

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(interviewRepository.findAllByJobApplication_OwnerOrderByScheduledAtAsc(currentUser))
                .thenReturn(List.of(interview));

        when(interviewMapper.toResponse(interview))
                .thenReturn(expectedResponse);

        List<InterviewResponse> responses =
                interviewService.getAllInterviews();

        assertEquals(1, responses.size());
        assertEquals(100L, responses.get(0).getId());

        verify(interviewRepository)
                .findAllByJobApplication_Owner(currentUser);
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
                interviewService.updateInterview(100L, request);

        assertEquals(100L, response.getId());
        assertEquals(request.getType(), interview.getType());
        assertEquals(request.getStatus(), interview.getStatus());
        assertEquals(request.getScheduledAt(), interview.getScheduledAt());

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
