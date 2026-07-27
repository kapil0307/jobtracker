package com.kapil.jobtracker.interview.repository;

import com.kapil.jobtracker.interview.entity.Interview;
import com.kapil.jobtracker.interview.entity.InterviewStatus;
import com.kapil.jobtracker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

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

    long countByJobApplication_OwnerAndStatus(User user, InterviewStatus status);
}
