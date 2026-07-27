package com.kapil.jobtracker.interview.dto;

import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.interview.entity.InterviewType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InterviewResponse {
    private Long id;
    private Long jobApplicationId;

    private InterviewType type;
    private InterviewStatus status;

    private LocalDateTime scheduledAt;

    private String meetingLink;
    private String notes;
    private String feedback;

    private Integer rating;
}
