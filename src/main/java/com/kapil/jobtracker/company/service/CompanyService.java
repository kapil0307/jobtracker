package com.kapil.jobtracker.company.service;

import com.kapil.jobtracker.company.dto.CompanyRequest;
import com.kapil.jobtracker.company.dto.CompanyResponse;
import com.kapil.jobtracker.company.entity.Company;
import com.kapil.jobtracker.company.exception.CompanyNotFoundException;
import com.kapil.jobtracker.company.mapper.CompanyMapper;
import com.kapil.jobtracker.company.repository.CompanyRepository;
import com.kapil.jobtracker.user.entity.User;
import com.kapil.jobtracker.user.exception.UserNotFoundException;
import com.kapil.jobtracker.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepo;
    private final CompanyMapper companyMapper;
    private final UserRepository userRepo;

    @Transactional
    public CompanyResponse createCompany(Long userId, CompanyRequest request){
        User user = userRepo.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User with Id: "+userId+" not found"));

        Company company = companyMapper.toCompany(request);
        company.setOwner(user);
        Company savedCompany = companyRepo.save(company);
        return companyMapper.toResponse(savedCompany);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long companyId, Long userId){
        Company company = companyRepo.findByIdAndOwnerId(companyId, userId)
                .orElseThrow(()-> new CompanyNotFoundException("Company with companyId: "+companyId+" not found"));

        return companyMapper.toResponse(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> getAllCompaniesByUser(Long userId){
        List<CompanyResponse> companies = companyRepo.findAllByOwnerId(userId).stream()
                .map(companyMapper::toResponse).toList();
        return companies;
    }

    @Transactional
    public CompanyResponse updateCompany(Long companyId, Long userId, CompanyRequest request){
        Company company = companyRepo.findByIdAndOwnerId(companyId, userId)
                .orElseThrow(()-> new CompanyNotFoundException("Company with companyId: "+companyId+" not found"));

        company.setName(request.getName());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());
        company.setIndustry(request.getIndustry());
        company.setNotes(request.getNotes());

        Company updatedCompany = companyRepo.save(company);
        return companyMapper.toResponse(updatedCompany);
    }

    @Transactional
    public void deleteCompany(Long companyId, Long userId){
        Company company = companyRepo.findByIdAndOwnerId(companyId, userId)
                .orElseThrow(()-> new CompanyNotFoundException("Company with companyId: "+companyId+" not found"));
        companyRepo.delete(company);
    }

}
