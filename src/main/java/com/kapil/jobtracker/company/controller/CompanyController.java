package com.kapil.jobtracker.company.controller;

import com.kapil.jobtracker.company.dto.CompanyRequest;
import com.kapil.jobtracker.company.dto.CompanyResponse;
import com.kapil.jobtracker.company.service.CompanyService;
import com.kapil.jobtracker.security.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class    CompanyController {
    private final CompanyService companyService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest request){
        Long userId = currentUserService.getCurrentUser().getId();

        CompanyResponse response= companyService.createCompany(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompaniesByUserId(){
        Long userId = currentUserService.getCurrentUser().getId();
        List<CompanyResponse> responses=companyService.getAllCompaniesByUser(userId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getCompanyByCompanyId(@PathVariable Long companyId){
        Long userId = currentUserService.getCurrentUser().getId();
        CompanyResponse response= companyService.getCompanyById(companyId, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long companyId,
                                                         @Valid @RequestBody CompanyRequest request){
        Long userId = currentUserService.getCurrentUser().getId();
        CompanyResponse response=companyService.updateCompany(companyId, userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId){
        Long userId = currentUserService.getCurrentUser().getId();
        companyService.deleteCompany(companyId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
