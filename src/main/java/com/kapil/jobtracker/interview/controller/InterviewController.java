package com.kapil.jobtracker.interview.controller;

import com.kapil.jobtracker.interview.dto.InterviewCreateRequest;
import com.kapil.jobtracker.interview.dto.InterviewResponse;
import com.kapil.jobtracker.interview.dto.InterviewUpdateRequest;
import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.interview.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(@Valid @RequestBody InterviewCreateRequest request){
        InterviewResponse response = interviewService.createInterview(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<InterviewResponse>> getAllInterviews(@ParameterObject Pageable pageable){
        return new  ResponseEntity<>(interviewService.getAllInterviews(pageable), HttpStatus.OK);
    }

    @GetMapping("/upcoming")
        public ResponseEntity<List<InterviewResponse>> getUpcomingInterviews(){
        return new ResponseEntity<>(interviewService.getUpcomingInterviews(), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<InterviewResponse> getInterviewById(@PathVariable Long id){
        return new ResponseEntity<>(interviewService.getInterviewById(id), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InterviewResponse>> getInterviewByStatus(@PathVariable InterviewStatus status){
        return new ResponseEntity<>(interviewService.getInterviewByStatus(status), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterviewResponse> updateInterview(@PathVariable Long id,
                                                             @Valid @RequestBody InterviewUpdateRequest request){
        return new ResponseEntity<>(interviewService.updateInterview(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable Long id){
        interviewService.deleteInterview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
