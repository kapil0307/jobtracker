package com.kapil.jobtracker.jobapplication.dto;

import com.kapil.jobtracker.jobapplication.entity.ApplicationSource;
import com.kapil.jobtracker.jobapplication.entity.ApplicationStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationRequest {

    @NotBlank(message = "Job title cannot be blank")
    @Size(max = 150, message = "Job title cannot exceed 150 characters")
    private String jobTitle;

    @NotNull(message = "Application status cannot be null")
    private ApplicationStatus status;

    @PastOrPresent(message = "Applied date cannot be in future")
    private LocalDate appliedDate;

    @Size(max = 500, message = "Job URL cannot exceed 500 characters")
    private String jobUrl;

    @Size(max = 150, message = "Location cannot exceed 150 characters")
    private String jobLocation;

    @Size(max = 100, message = "Salary range cannot exceed 100 characters")
    private String salaryRange;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    @NotNull(message = "CompanyId cannot be null")
    private Long companyId;

    @NotNull(message = "Application source cannot be null")
    private ApplicationSource source;
}
