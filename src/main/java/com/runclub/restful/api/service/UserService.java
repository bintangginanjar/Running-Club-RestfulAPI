package com.runclub.restful.api.service;

import java.util.Collections;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.runclub.restful.api.entity.RoleEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.mapper.ResponseMapper;
import com.runclub.restful.api.model.RegisterUserRequest;
import com.runclub.restful.api.model.UpdateUserRequest;
import com.runclub.restful.api.model.UserResponse;
import com.runclub.restful.api.repository.RoleRepository;
import com.runclub.restful.api.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ValidationService validationService;    

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
            ValidationService validationService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.validationService = validationService;
    }

    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        validationService.validate(request);

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        RoleEntity role = roleRepository.findByName(request.getRole()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roles not found"));

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        //user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singletonList(role));

        userRepository.save(user);

        return ResponseMapper.ToUserResponseMapper(user);
    }

    @Transactional(readOnly = true)
    public UserResponse get(Authentication authentication) {

        UserEntity user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseMapper.ToUserResponseMapper(user);
    }

    @Transactional
    public UserResponse update(Authentication authentication, UpdateUserRequest request) {
        validationService.validate(request);

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            
        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        return ResponseMapper.ToUserResponseMapper(user);
    }
}
