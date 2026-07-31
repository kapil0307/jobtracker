package com.kapil.jobtracker.interview.dto;

import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.interview.entity.InterviewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewUpdateRequest {

    @NotNull(message = "Interview type cannot be null")
    private InterviewType type;

    @NotNull(message = "Interview status cannot be null")
    private InterviewStatus status;

    @NotNull(message = "Scheduled date cannot be null")
    private LocalDateTime scheduledAt;

    private String meetingLink;
    private String notes;
    private String feedback;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer rating;
}

