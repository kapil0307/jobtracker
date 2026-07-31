package com.kapil.jobtracker.interview.repository;

import com.kapil.jobtracker.interview.entity.Interview;
import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    Optional<Interview> findByIdAndJobApplication_Owner(
            Long interviewId,
            User user
    );

    List<Interview> findAllByJobApplication_Owner(User user);

    List<Interview> findAllByJobApplication_OwnerOrderByScheduledAtAsc(User user);

    List<Interview> findAllByJobApplication_OwnerAndStatusOrderByScheduledAtAsc(
            User owner,
            InterviewStatus status
    );

    List<Interview> findAllByJobApplication_OwnerAndStatusAndScheduledAtAfterOrderByScheduledAtAsc(
            User owner,
            InterviewStatus status,
            LocalDateTime currentTime
    );

    long countByJobApplication_OwnerAndStatus(User user, InterviewStatus status);

    Page<Interview> findAllByJobApplication_Owner(
            User owner,
            Pageable pageable
    );
}
