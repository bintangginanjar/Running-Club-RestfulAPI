package com.runclub.restful.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runclub.restful.api.entity.RoleEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.model.LoginUserRequest;
import com.runclub.restful.api.model.TokenResponse;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.repository.RoleRepository;
import com.runclub.restful.api.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testLoginSuccess() throws Exception {
        RoleEntity role = roleRepository.findByName("USER").orElse(null);

        UserEntity user = new UserEntity();
        user.setUsername("bintang");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("bintang");
        request.setPassword("password");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                        
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            /*
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            UserEntity userDb = userRepository.findByUsername("bintang").orElse(null);
            assertNotNull(userDb);
            assertEquals(userDb.getToken(), response.getData().getToken());
            assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
             */
        });
    }
}
