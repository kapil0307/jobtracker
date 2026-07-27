package com.kapil.jobtracker.user.service;

import com.kapil.jobtracker.user.dto.RegisterRequest;
import com.kapil.jobtracker.user.dto.UserResponse;
import com.kapil.jobtracker.user.entity.Role;
import com.kapil.jobtracker.user.entity.RoleName;
import com.kapil.jobtracker.user.entity.User;
import com.kapil.jobtracker.user.exception.EmailAlreadyRegisteredException;
import com.kapil.jobtracker.user.exception.RoleNotFoundException;
import com.kapil.jobtracker.user.exception.UserNotFoundException;
import com.kapil.jobtracker.user.mapper.UserMapper;
import com.kapil.jobtracker.user.repository.RoleRepository;
import com.kapil.jobtracker.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final UserMapper userMapper;

    //Security
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerUser(RegisterRequest request){
        if(userRepo.existsByEmail(request.getEmail())){
            throw new EmailAlreadyRegisteredException("This Email is already registered");
        }
        User user =  userMapper.toUser(request);
        Role defaultRole = roleRepo.findByName(RoleName.ROLE_USER)
                .orElseThrow(()-> new RoleNotFoundException("Default role not found"));
        user.getRoles().add(defaultRole);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail().trim().toLowerCase());
        User savedUser = userRepo.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long id){
        User user = userRepo.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User with Id: "+id+" not found"));
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers(){
        return userRepo.findAll().stream().map(userMapper::toResponse).toList();
    }
}
