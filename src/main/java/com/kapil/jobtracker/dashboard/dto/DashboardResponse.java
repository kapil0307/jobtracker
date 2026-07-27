package com.kapil.jobtracker.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private String userName;
    private long totalCompanies;
    private long totalApplication;
    private long scheduledInterviews;
    private long completedInterviews;
    private long cancelledInterviews;
}
