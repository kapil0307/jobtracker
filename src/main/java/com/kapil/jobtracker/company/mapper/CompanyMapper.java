package com.kapil.jobtracker.company.mapper;

import com.kapil.jobtracker.company.dto.CompanyRequest;
import com.kapil.jobtracker.company.dto.CompanyResponse;
import com.kapil.jobtracker.company.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public Company toCompany(CompanyRequest request){
        Company company=new Company();

        company.setName(request.getName());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());
        company.setIndustry(request.getIndustry());
        company.setNotes(request.getNotes());

        return company;
    }

    public CompanyResponse toResponse(Company company){
        CompanyResponse response=new CompanyResponse();

        response.setId(company.getId());
        response.setName(company.getName());
        response.setWebsite(company.getWebsite());
        response.setLocation(company.getLocation());
        response.setIndustry(company.getIndustry());
        response.setNotes(company.getNotes());
        response.setOwnerId(company.getOwner().getId());
        response.setOwnerName(company.getOwner().getName());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());

        return response;
    }
}
