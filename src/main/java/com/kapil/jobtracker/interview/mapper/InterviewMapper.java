package com.kapil.jobtracker.interview.mapper;

import com.kapil.jobtracker.interview.dto.InterviewCreateRequest;
import com.kapil.jobtracker.interview.dto.InterviewResponse;
import com.kapil.jobtracker.interview.entity.Interview;
import org.springframework.stereotype.Component;

@Component
public class InterviewMapper {
    
    public Interview toEntity(InterviewCreateRequest request){
        return Interview.builder()
                .type(request.getType())
                .status(request.getStatus())
                .scheduledAt(request.getScheduledAt())
                .meetingLink(request.getMeetingLink())
                .notes(request.getNotes())
                .feedback(request.getFeedback())
                .rating(request.getRating())
                .build();
    }

    public InterviewResponse toResponse(Interview interview){
        return new InterviewResponse(
                interview.getId(),
                interview.getJobApplication().getId(),
                interview.getType(),
                interview.getStatus(),
                interview.getScheduledAt(),
                interview.getMeetingLink(),
                interview.getNotes(),
                interview.getFeedback(),
                interview.getRating()
        );
    }
}
