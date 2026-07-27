package com.kapil.jobtracker.company.entity;

import com.kapil.jobtracker.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="companies")
public class Company {

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        private Long id;

        @Column(nullable = false, length = 150)
        private String name;

        @Column(length=255)
        private String website;

        @Column(length = 150)
        private String location;

        @Column(length = 100)
        private String industry;

        @Column(columnDefinition = "TEXT")
        private String notes;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "owner_id", nullable = false)
        private User owner;

        @Column(name = "created_at" ,nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at" ,nullable = false)
        private LocalDateTime updatedAt;

        @PrePersist
        public void beforeCreate(){
            createdAt=LocalDateTime.now();
            updatedAt=LocalDateTime.now();
        }

        @PreUpdate
        public void beforeUpdate(){
            updatedAt=LocalDateTime.now();
        }

}
