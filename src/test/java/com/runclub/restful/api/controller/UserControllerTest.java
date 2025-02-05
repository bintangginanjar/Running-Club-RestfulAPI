package com.runclub.restful.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runclub.restful.api.entity.RoleEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.model.RegisterUserRequest;
import com.runclub.restful.api.model.UpdateRoleRequest;
import com.runclub.restful.api.model.UpdateUserRequest;
import com.runclub.restful.api.model.UserResponse;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.repository.ClubRepository;
import com.runclub.restful.api.repository.RoleRepository;
import com.runclub.restful.api.repository.UserRepository;
import com.runclub.restful.api.security.JwtUtil;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final String username = "test";
    private final String password = "rahasia";

    @BeforeEach
    void setUp() {
        //roleRepository.deleteAll();
        clubRepository.deleteAll();
        userRepository.deleteAll();

        /*
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        RoleEntity userRole = new RoleEntity();
        userRole.setName("ROLE_USER");
        roleRepository.save(userRole);
        */
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setRole("ROLE_USER");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testCreateUserBlank() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername(username);
        request.setPassword("");
        request.setRole("USER");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                        
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetUserSuccess() throws Exception {                        
        RoleEntity role = roleRepository.findByName("ROLE_USER").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));
        //user.addRole(role);
        userRepository.save(user);
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                                             
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(username, response.getData().getUsername());            
        });
    }

    @Test
    void testGetUserInvalidToken() throws Exception {        
        String username = "test";
        String password = "password";
        
        RoleEntity role = roleRepository.findByName("ROLE_USER").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));
        //user.addRole(role);
        userRepository.save(user);
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)                      
                        .header("Authorization", mockBearerToken)                                             
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testUpdateUserPasswordSuccess() throws Exception {        
        String username = "test";
        String password = "password";
        
        RoleEntity role = roleRepository.findByName("ROLE_USER").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));
        //user.addRole(role);
        userRepository.save(user);
        
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("123456");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                    
                        .header("Authorization", mockBearerToken)                                             
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());                     
        });
    }

    @Test
    void testUpdateUserInvalidToken() throws Exception {                
        RoleEntity role = roleRepository.findByName("ROLE_USER").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);        
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));
        //user.addRole(role);
        userRepository.save(user);
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("123456");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                    
                        .header("Authorization", mockBearerToken)                                             
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());                 
        });
    }

    @Test
    void testUpdateUserRoleSuccess() throws Exception {      
        String adminUsername = "admin";
        String adminPassword = "password";
                    
        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);        

        UserEntity admin = new UserEntity();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRoles(Collections.singletonList(adminRole));
        //admin.addRole(adminRole);
        userRepository.save(admin);        

        RoleEntity userRole = roleRepository.findByName("ROLE_USER").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(userRole));
        //user.addRole(userRole);
        userRepository.save(user);  

        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setUsername(username);
        request.setRole("ROLE_ADMIN");

        mockMvc.perform(
                patch("/api/users/role")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                    
                        .header("Authorization", mockBearerToken)                                             
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());                     
        });
    }
}
