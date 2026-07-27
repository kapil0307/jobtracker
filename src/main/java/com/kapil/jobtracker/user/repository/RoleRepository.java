package com.kapil.jobtracker.user.repository;

import com.kapil.jobtracker.user.entity.Role;
import com.kapil.jobtracker.user.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
