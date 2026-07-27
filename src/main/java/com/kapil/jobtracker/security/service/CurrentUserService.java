package com.kapil.jobtracker.security.service;

import com.kapil.jobtracker.user.entity.User;
import com.kapil.jobtracker.user.exception.UserNotFoundException;
import com.kapil.jobtracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepo;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepo.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("Logged-in user not found"));
    }
}
