package com.kapil.jobtracker.jobapplication.repository;

import com.kapil.jobtracker.company.entity.Company;
import com.kapil.jobtracker.interview.entity.Interview;
import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.jobapplication.entity.JobApplication;
import com.kapil.jobtracker.user.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Optional<JobApplication> findByIdAndOwnerId(Long applicationId, Long ownerId);

    List<JobApplication> findAllByOwnerId(Long ownerId);

    Optional<JobApplication> findByIdAndOwner(Long applicationId, User user);

    boolean existsByOwnerId(Long ownerId);
    boolean existsByOwnerAndCompanyAndJobTitleIgnoreCase(User user, Company company, String jobTitle);

    long countByOwner(User user);
}
