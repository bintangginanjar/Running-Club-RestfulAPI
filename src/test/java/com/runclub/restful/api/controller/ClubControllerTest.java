package com.runclub.restful.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.runclub.restful.api.model.ClubResponse;
import com.runclub.restful.api.model.RegisterClubRequest;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.repository.ClubRepository;
import com.runclub.restful.api.repository.RoleRepository;
import com.runclub.restful.api.repository.UserRepository;
import com.runclub.restful.api.security.JwtUtil;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class ClubControllerTest {
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

    private final String password = "password";

    @BeforeEach
    void setUp() {        
        clubRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity role = roleRepository.findByName("USER").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);
    }

    @Test
    void testCreateClubSuccess() throws Exception {        
        RegisterClubRequest request = new RegisterClubRequest();
        request.setTitle("Test Club");
        request.setContent("Test Content");
        request.setPhotoUrl("Test PhotoUrl");
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/clubs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ClubResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(request.getTitle(), response.getData().getTitle());
            assertEquals(request.getContent(), response.getData().getContent());
            assertEquals(request.getPhotoUrl(), response.getData().getPhotoUrl());
        });
    }

    @Test
    void testCreateClubNoToken() throws Exception {        
        RegisterClubRequest request = new RegisterClubRequest();
        request.setTitle("Test Club");
        request.setContent("Test Content");
        request.setPhotoUrl("Test PhotoUrl");            

        mockMvc.perform(
                post("/api/clubs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                             
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ClubResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }
}
