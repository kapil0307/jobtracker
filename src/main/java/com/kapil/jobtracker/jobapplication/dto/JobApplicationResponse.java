package com.kapil.jobtracker.jobapplication.dto;


import com.kapil.jobtracker.jobapplication.entity.ApplicationSource;
import com.kapil.jobtracker.jobapplication.entity.ApplicationStatus;
import com.kapil.jobtracker.jobapplication.entity.JobApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationResponse {
    private Long id;

    private String jobTitle;

    private ApplicationStatus status;

    private LocalDate appliedDate;

    private String jobUrl;

    private String jobLocation;

   private String salaryRange;

    private String notes;

    private Long companyId;
    private String companyName;

    private Long ownerId;
    private String ownerName;

    private ApplicationSource source;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
