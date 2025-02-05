package com.runclub.restful.api.service;

import java.util.Collections;
import java.util.List;
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
import com.runclub.restful.api.model.RoleResponse;
import com.runclub.restful.api.model.UpdateRoleRequest;
import com.runclub.restful.api.model.UpdateUserRequest;
import com.runclub.restful.api.model.UserResponse;
import com.runclub.restful.api.repository.RoleRepository;
import com.runclub.restful.api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //user.addRole(role);
        user.setRoles(Collections.singletonList(role));        

        userRepository.save(user);

        //UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());

        //List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        List<RoleResponse> roles = ResponseMapper.ToRoleResponseList(user.getRoles());

        return ResponseMapper.ToUserResponseMapper(user, roles);
    }

    @Transactional(readOnly = true)
    public UserResponse get(Authentication authentication) {

        UserEntity user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        //UserDetails userDetails = userDetailService.loadUserByUsername(user.getUsername());

        //List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        List<RoleResponse> roles = ResponseMapper.ToRoleResponseList(user.getRoles());

        return ResponseMapper.ToUserResponseMapper(user, roles);
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

        //UserDetails userDetails = userDetailService.loadUserByUsername(user.getUsername());

        //List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        List<RoleResponse> roles = ResponseMapper.ToRoleResponseList(user.getRoles());

        return ResponseMapper.ToUserResponseMapper(user, roles);
    }
    
    @Transactional
    public UserResponse updateRole(Authentication authentication, UpdateRoleRequest request) {
        validationService.validate(request);        
                
        UserEntity user = userRepository.findByUsername(request.getUsername())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        RoleEntity role = roleRepository.findByName(request.getRole())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        log.info("Username : " + user.getUsername());
        log.info("User ID : " + user.getId());
        log.info("Role : " + role.getName());
        log.info("Role ID : " + role.getId());

        int update = userRepository.updateUserRole(user.getId(), role.getId());

        log.info("update : " + update);                
        
        if (update != 0) {            
            log.info("Update role");
            
            //UserDetails userDetails = userDetailService.loadUserByUsername(user.getUsername());
            
            //List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            UserEntity updatedUser = userRepository.findByUsername(request.getUsername())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            List<RoleResponse> roles = ResponseMapper.ToRoleResponseList(updatedUser.getRoles());
            
            return ResponseMapper.ToUserResponseMapper(updatedUser, roles);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Update role failed"); 
        }                                    
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list(Authentication authentication) {
        List<UserEntity> users = userRepository.findAll();

        return ResponseMapper.ToUserResponseListMapper(users);
    }
    
}
