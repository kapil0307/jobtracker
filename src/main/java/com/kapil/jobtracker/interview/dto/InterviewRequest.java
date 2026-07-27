package com.kapil.jobtracker.interview.dto;

import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.interview.entity.InterviewType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class InterviewRequest {

    @NotNull(message = "Job application id cannot be null")
    private Long jobApplicationId;

    @NotNull(message = "Interview type cannot be null")
    private InterviewType type;

    @NotNull(message = "Interview status cannot be null")
    private InterviewStatus status;

    @NotNull(message = "Scheduled Date cannot be null")
    @Future(message = "Scheduled Date cannot be in past")
    private LocalDateTime scheduledAt;


    private String meetingLink;
    private String notes;
    private String feedback;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer rating;
}
