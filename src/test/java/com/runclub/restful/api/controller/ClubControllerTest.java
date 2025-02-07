package com.runclub.restful.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

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
import com.runclub.restful.api.entity.ClubEntity;
import com.runclub.restful.api.entity.RoleEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.model.ClubResponse;
import com.runclub.restful.api.model.RegisterClubRequest;
import com.runclub.restful.api.model.UpdateClubRequest;
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

    private final String adminUsername = "testadmin";
    private final String adminPassword = "password";

    private final String clubTitle = "Test Club";
    private final String clubContent = "Test Club Content";
    private final String clubPhotoUrl = "https://media.istockphoto.com/id/1142900322/photo/happy-female-runner-jogging-in-the-morning-in-nature.jpg?b=1&s=612x612&w=0&k=20&c=6yDa1kkxNOObyu7O6LJprQSgB454ME3IjOgmdZzciHg=";

    @BeforeEach
    void setUp() {        
        clubRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
        RoleEntity userRole = roleRepository.findByName("ROLE_USER").orElse(null);

        UserEntity admin = new UserEntity();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRoles(Collections.singletonList(adminRole));
        //admin.addRole(adminRole);
        userRepository.save(admin);        

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(userRole));
        //user.addRole(userRole);
        userRepository.save(user);        
                
        ClubEntity club = new ClubEntity();
        club.setTitle(clubTitle);
        club.setContent(clubContent);
        club.setPhotoUrl(clubPhotoUrl);
        club.setCreatedBy(user);
        clubRepository.save(club);
    }
    
    @Test
    void testCreateClubSuccess() throws Exception {        
        RegisterClubRequest request = new RegisterClubRequest();
        request.setTitle("Daytona");
        request.setContent("Daytona Content");
        request.setPhotoUrl("https://img.freepik.com/free-photo/young-attractive-fitness-girl-jogging_176420-824.jpg");
        
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
    void testCreateClubInvalidToken() throws Exception {        
        RegisterClubRequest request = new RegisterClubRequest();
        request.setTitle("Nevada Club");
        request.setContent("Nevada Content");
        request.setPhotoUrl("Nevada PhotoUrl");
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                post("/api/clubs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                                             
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ClubResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateClubNoToken() throws Exception {        
        RegisterClubRequest request = new RegisterClubRequest();
        request.setTitle("Nevada Club");
        request.setContent("Nevada Content");
        request.setPhotoUrl("Nevada PhotoUrl");         

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

    @Test
    void testGetClubSuccess() throws Exception { 
        ClubEntity club = clubRepository.findByTitle(clubTitle).orElse(null);
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;
    
        mockMvc.perform(
                get("/api/clubs/" + club.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<ClubResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetClubWrongToken() throws Exception { 
        ClubEntity club = clubRepository.findByTitle(clubTitle).orElse(null);
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";
    
        mockMvc.perform(
                get("/api/clubs/" + club.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<ClubResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetClubIdNotFound() throws Exception {                 
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;
    
        mockMvc.perform(
                get("/api/clubs/4")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<ClubResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetClubBadClubId() throws Exception {                 
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;
    
        mockMvc.perform(
                get("/api/clubs/1a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<ClubResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetClubNoToken() throws Exception { 
        ClubEntity club = clubRepository.findByTitle(clubTitle).orElse(null);

        mockMvc.perform(
                get("/api/clubs/" + club.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                    
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<ClubResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateClubSuccess() throws Exception {       
        ClubEntity club = clubRepository.findByTitle(clubTitle).orElse(null);
        
        UpdateClubRequest request = new UpdateClubRequest();
        request.setTitle("Test Club Update");
        request.setContent("Test Content Update");
        request.setPhotoUrl("Test PhotoUrl Update");
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/clubs/" + club.getId())
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
    void testDeleteClubSuccess() throws Exception {       
        ClubEntity club = clubRepository.findByTitle(clubTitle).orElse(null);

        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                delete("/api/clubs/" + club.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());            
        });
    }

    @Test
    void testDeleteClubBadClubId() throws Exception {           
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                delete("/api/clubs/1a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteClubIdNotFound() throws Exception {           
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                delete("/api/clubs/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteClubInvalidToken() throws Exception {          
        ClubEntity club = clubRepository.findByTitle(clubTitle).orElse(null);

        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                delete("/api/clubs/" + club.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testDeleteClubNoToken() throws Exception {          
        ClubEntity club = clubRepository.findByTitle(clubTitle).orElse(null);

        mockMvc.perform(
                delete("/api/clubs/" + club.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                     
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());            
        });
    }

    @Test
    void testGetAllClubSuccess() throws Exception {                 
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;
    
        mockMvc.perform(
                get("/api/clubs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<ClubResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetAllClubBadRole() throws Exception {                 
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;
    
        mockMvc.perform(
                get("/api/clubs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<ClubResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetAllClubInvalidToken() throws Exception {                 
        Authentication authentication = authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";
    
        mockMvc.perform(
                get("/api/clubs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ClubResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetListClubSuccess() throws Exception {                 
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;
    
        mockMvc.perform(
                get("/api/clubs/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<ClubResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetListClubInvalidToken() throws Exception {                 
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";
    
        mockMvc.perform(
                get("/api/clubs/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<ClubResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetListClubNoToken() throws Exception {                       
        mockMvc.perform(
                get("/api/clubs/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                             
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<ClubResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }
}
