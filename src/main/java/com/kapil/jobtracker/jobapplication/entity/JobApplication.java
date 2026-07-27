package com.kapil.jobtracker.jobapplication.entity;

import com.kapil.jobtracker.company.entity.Company;
import com.kapil.jobtracker.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="job_title", nullable = false, length = 150)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationStatus status;

    @Column(name="applied_date")
    private LocalDate appliedDate;

    @Column(name = "job_url", length = 500)
    private String jobUrl;

    @Column(name = "job_location" ,length = 150)
    private String jobLocation;

    @Column(name="salary_range", length = 100)
    private String salaryRange;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ApplicationSource source;

    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeCreate(){
        updatedAt=LocalDateTime.now();
        createdAt=LocalDateTime.now();
    }

    @PreUpdate
    public void beforeUpdate(){
        updatedAt=LocalDateTime.now();
    }
}
