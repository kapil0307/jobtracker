package com.kapil.jobtracker.company.repository;

import com.kapil.jobtracker.company.entity.Company;
import com.kapil.jobtracker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByIdAndOwnerId(Long companyId, Long ownerId);

    List<Company> findAllByOwnerId(Long ownerId);

    long countByOwner(User owner);
}
