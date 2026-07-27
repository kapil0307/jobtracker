package com.kapil.jobtracker.dashboard.controller;

import com.kapil.jobtracker.dashboard.dto.DashboardResponse;
import com.kapil.jobtracker.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboardStatistics(){
        return new ResponseEntity<>(dashboardService.getDashboardStatistics(), HttpStatus.OK);
    }
}
