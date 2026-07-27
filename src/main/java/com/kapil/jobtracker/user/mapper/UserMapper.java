package com.kapil.jobtracker.user.mapper;

import com.kapil.jobtracker.user.dto.RegisterRequest;
import com.kapil.jobtracker.user.dto.UserResponse;
import com.kapil.jobtracker.user.entity.Role;
import com.kapil.jobtracker.user.entity.RoleName;
import com.kapil.jobtracker.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toUser(RegisterRequest request){
        User user= new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return user;
    }

    public UserResponse toResponse(User user){
        UserResponse response= new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        Set<RoleName> roleNames = user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());

        response.setRoles(roleNames);


        return response;
    }
}
