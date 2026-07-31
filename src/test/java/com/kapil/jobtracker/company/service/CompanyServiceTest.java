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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepo;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private CompanyService companyService;

    private User user;
    private Company company;
    private CompanyRequest companyRequest;
    private CompanyResponse companyResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Kapil");

        company = new Company();
        company.setId(10L);
        company.setName("Google");
        company.setWebsite("https://google.com");
        company.setLocation("Bengaluru");
        company.setIndustry("Technology");
        company.setNotes("Product company");
        company.setOwner(user);

        companyRequest = new CompanyRequest(
                "Google",
                "https://google.com",
                "Bengaluru",
                "Technology",
                "Product company"
        );

        companyResponse = new CompanyResponse(
                10L,
                "Google",
                "https://google.com",
                "Bengaluru",
                "Technology",
                "Product company",
                1L,
                "Kapil",
                null,
                null
        );
    }

    @Test
    void createCompany_shouldCreateCompanySuccessfully() {
        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        when(companyMapper.toCompany(companyRequest))
                .thenReturn(company);

        when(companyRepo.save(company))
                .thenReturn(company);

        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        CompanyResponse result =
                companyService.createCompany(1L, companyRequest);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Google", result.getName());
        assertEquals(user, company.getOwner());

        verify(userRepo).findById(1L);
        verify(companyMapper).toCompany(companyRequest);
        verify(companyRepo).save(company);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void createCompany_shouldThrowExceptionWhenUserNotFound() {
        when(userRepo.findById(1L))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> companyService.createCompany(1L, companyRequest)
        );

        assertEquals(
                "User with Id: 1 not found",
                exception.getMessage()
        );

        verify(userRepo).findById(1L);
        verify(companyRepo, never()).save(any());
        verifyNoInteractions(companyMapper);
    }

    @Test
    void getCompanyById_shouldReturnCompanySuccessfully() {
        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.of(company));

        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        CompanyResponse result =
                companyService.getCompanyById(10L, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Google", result.getName());

        verify(companyRepo).findByIdAndOwnerId(10L, 1L);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void getCompanyById_shouldThrowExceptionWhenCompanyNotFound() {
        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.getCompanyById(10L, 1L)
        );

        assertEquals(
                "Company with companyId: 10 not found",
                exception.getMessage()
        );

        verify(companyRepo).findByIdAndOwnerId(10L, 1L);
        verifyNoInteractions(companyMapper);
    }

    @Test
    void getAllCompaniesByUser_shouldReturnCompaniesSuccessfully() {
        Company secondCompany = new Company();
        secondCompany.setId(11L);
        secondCompany.setName("Microsoft");
        secondCompany.setOwner(user);

        CompanyResponse secondResponse = new CompanyResponse();
        secondResponse.setId(11L);
        secondResponse.setName("Microsoft");

        when(companyRepo.findAllByOwnerId(1L))
                .thenReturn(List.of(company, secondCompany));

        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        when(companyMapper.toResponse(secondCompany))
                .thenReturn(secondResponse);

        List<CompanyResponse> result =
                companyService.getAllCompaniesByUser(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Google", result.get(0).getName());
        assertEquals("Microsoft", result.get(1).getName());

        verify(companyRepo).findAllByOwnerId(1L);
        verify(companyMapper).toResponse(company);
        verify(companyMapper).toResponse(secondCompany);
    }

    @Test
    void getAllCompaniesByUser_shouldReturnEmptyList() {
        when(companyRepo.findAllByOwnerId(1L))
                .thenReturn(List.of());

        List<CompanyResponse> result =
                companyService.getAllCompaniesByUser(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(companyRepo).findAllByOwnerId(1L);
        verifyNoInteractions(companyMapper);
    }

    @Test
    void updateCompany_shouldUpdateCompanySuccessfully() {
        CompanyRequest updateRequest = new CompanyRequest(
                "Updated Google",
                "https://careers.google.com",
                "Hyderabad",
                "Software",
                "Updated notes"
        );

        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.of(company));

        when(companyRepo.save(company))
                .thenReturn(company);

        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        CompanyResponse result =
                companyService.updateCompany(
                        10L,
                        1L,
                        updateRequest
                );

        assertNotNull(result);
        assertEquals("Updated Google", company.getName());
        assertEquals(
                "https://careers.google.com",
                company.getWebsite()
        );
        assertEquals("Hyderabad", company.getLocation());
        assertEquals("Software", company.getIndustry());
        assertEquals("Updated notes", company.getNotes());

        verify(companyRepo).findByIdAndOwnerId(10L, 1L);
        verify(companyRepo).save(company);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void updateCompany_shouldThrowExceptionWhenCompanyNotFound() {
        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.updateCompany(
                        10L,
                        1L,
                        companyRequest
                )
        );

        verify(companyRepo).findByIdAndOwnerId(10L, 1L);
        verify(companyRepo, never()).save(any());
        verifyNoInteractions(companyMapper);
    }

    @Test
    void deleteCompany_shouldDeleteCompanySuccessfully() {
        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.of(company));

        companyService.deleteCompany(10L, 1L);

        verify(companyRepo).findByIdAndOwnerId(10L, 1L);
        verify(companyRepo).delete(company);
    }

    @Test
    void deleteCompany_shouldThrowExceptionWhenCompanyNotFound() {
        when(companyRepo.findByIdAndOwnerId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.deleteCompany(10L, 1L)
        );

        verify(companyRepo).findByIdAndOwnerId(10L, 1L);
        verify(companyRepo, never()).delete(any());
    }
}