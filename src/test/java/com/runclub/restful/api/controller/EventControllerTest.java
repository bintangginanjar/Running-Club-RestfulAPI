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
import com.runclub.restful.api.entity.EventEntity;
import com.runclub.restful.api.entity.RoleEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.model.EventResponse;
import com.runclub.restful.api.model.RegisterEventRequest;
import com.runclub.restful.api.model.UpdateEventRequest;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.repository.ClubRepository;
import com.runclub.restful.api.repository.EventRepository;
import com.runclub.restful.api.repository.RoleRepository;
import com.runclub.restful.api.repository.UserRepository;
import com.runclub.restful.api.security.JwtUtil;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EventRepository eventRepository;

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

    private final String eventName = "Test Event";
    private final String eventStartTime = "10-02-2025";
    private final String eventEndTime = "10-02-2025";
    private final String eventType = "Event Type Test";
    private final String eventPhotoUrl = "https://media.istockphoto.com/id/1142900322/photo/happy-female-runner-jogging-in-the-morning-in-nature.jpg?b=1&s=612x612&w=0&k=20&c=6yDa1kkxNOObyu7O6LJprQSgB454ME3IjOgmdZzciHg=";

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        clubRepository.deleteAll();
        userRepository.deleteAll();

        RoleEntity adminRole = roleRepository.findByName("ADMIN").orElse(null);
        RoleEntity userRole = roleRepository.findByName("USER").orElse(null);

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
    void testCreateEventSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        RegisterEventRequest request = new RegisterEventRequest();
        request.setName(eventName);
        request.setStartTime(eventStartTime);
        request.setEndTime(eventEndTime);
        request.setType(eventType);
        request.setPhotoUrl(eventPhotoUrl);           
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/clubs/" + club.getId() + "/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testCreateEventBlank() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        RegisterEventRequest request = new RegisterEventRequest();
        request.setName("");
        request.setStartTime(eventStartTime);
        request.setEndTime(eventEndTime);
        request.setType(eventType);
        request.setPhotoUrl(eventPhotoUrl);           
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/clubs/" + club.getId() + "/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateEventInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        RegisterEventRequest request = new RegisterEventRequest();
        request.setName("");
        request.setStartTime(eventStartTime);
        request.setEndTime(eventEndTime);
        request.setType(eventType);
        request.setPhotoUrl(eventPhotoUrl);           
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                post("/api/clubs/" + club.getId() + "/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateEventNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        RegisterEventRequest request = new RegisterEventRequest();
        request.setName(eventName);
        request.setStartTime(eventStartTime);
        request.setEndTime(eventEndTime);
        request.setType(eventType);
        request.setPhotoUrl(eventPhotoUrl);                         

        mockMvc.perform(
                post("/api/clubs/" + club.getId() + "/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                                                  
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateEventBadClubId() throws Exception {        
        RegisterEventRequest request = new RegisterEventRequest();
        request.setName(eventName);
        request.setStartTime(eventStartTime);
        request.setEndTime(eventEndTime);
        request.setType(eventType);
        request.setPhotoUrl(eventPhotoUrl);
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/clubs/1a/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", mockBearerToken)                                                 
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateEventClubNotFound() throws Exception {        
        RegisterEventRequest request = new RegisterEventRequest();
        request.setName(eventName);
        request.setStartTime(eventStartTime);
        request.setEndTime(eventEndTime);
        request.setType(eventType);
        request.setPhotoUrl(eventPhotoUrl);           
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                post("/api/clubs/1/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetEventSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
                          
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/clubs/" + club.getId() + "/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
        });
    }

    @Test
    void testGetEventBadClubId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
                          
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/clubs/1a/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetEventClubNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
                          
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/clubs/1/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetEventBadEventId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
                          
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/clubs/" + club.getId() + "/events/1a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetEventEventNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
                          
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/clubs/" + club.getId() + "/events/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetEventInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
                          
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/clubs/" + club.getId() + "/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testGetEventNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
                              
        mockMvc.perform(
                get("/api/clubs/" + club.getId() + "/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                             
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
        });
    }

    @Test
    void testUpdateEventSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
        
        UpdateEventRequest request = new UpdateEventRequest();
        request.setName("Daytona Event");
        request.setStartTime("10-03-2025");
        request.setEndTime("10-03-2025");
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/clubs/" + club.getId() + "/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertEquals(request.getName(), response.getData().getName());
            assertEquals(request.getStartTime(), response.getData().getStartTime());
            assertEquals(request.getEndTime(), response.getData().getEndTime());
        });
    }

    @Test
    void testUpdateEventBadClubId() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
        
        UpdateEventRequest request = new UpdateEventRequest();
        request.setName("Daytona Event");
        request.setStartTime("10-03-2025");
        request.setEndTime("10-03-2025");
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/clubs/1a/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateEventClubNotFound() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
        
        UpdateEventRequest request = new UpdateEventRequest();
        request.setName("Daytona Event");
        request.setStartTime("10-03-2025");
        request.setEndTime("10-03-2025");
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/clubs/1/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateEventBadEvent() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
        
        UpdateEventRequest request = new UpdateEventRequest();
        request.setName("Daytona Event");
        request.setStartTime("10-03-2025");
        request.setEndTime("10-03-2025");
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                patch("/api/clubs/" + club.getId() + "/events/1a")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateEventInvalidToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
        
        UpdateEventRequest request = new UpdateEventRequest();
        request.setName("Daytona Event");
        request.setStartTime("10-03-2025");
        request.setEndTime("10-03-2025");
        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                patch("/api/clubs/" + club.getId() + "/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) 
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateEventNoToken() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
        
        UpdateEventRequest request = new UpdateEventRequest();
        request.setName("Daytona Event");
        request.setStartTime("10-03-2025");
        request.setEndTime("10-03-2025");        

        mockMvc.perform(
                patch("/api/clubs/1/events/" + event.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))                                           
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<EventResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testDeleteEventSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        ClubEntity club = clubRepository.findFirstByCreatedByAndTitle(user, clubTitle).orElse(null);

        EventEntity event = new EventEntity();
        event.setName(eventName);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setType(eventType);
        event.setPhotoUrl(eventPhotoUrl);
        event.setClub(club);
        event.setCreatedBy(user);
        eventRepository.save(event);
                          
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                delete("/api/clubs/" + club.getId() + "/events/" + event.getId())
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
    void testGetAllEventSuccess() throws Exception {        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<EventResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetAllEventBadRole() throws Exception {        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                username, password)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<EventResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetAllEventInvalidToken() throws Exception {        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<EventResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testListEventSuccess() throws Exception {        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken;

        mockMvc.perform(
                get("/api/events/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                WebResponse<List<EventResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(true, response.getStatus());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testListEventInvalidToken() throws Exception {        
        Authentication authentication =  authenticationManager.authenticate(
                                            new UsernamePasswordAuthenticationToken(
                                                adminUsername, adminPassword)
                                            );

        String mockToken = jwtUtil.generateToken(authentication);
        String mockBearerToken = "Bearer " + mockToken + "a";

        mockMvc.perform(
                get("/api/events/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                        
                        .header("Authorization", mockBearerToken)                       
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                WebResponse<List<EventResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testListEventNoToken() throws Exception {                
        mockMvc.perform(
                get("/api/events/list")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)                                                                   
        ).andExpectAll(
                status().isForbidden()
        ).andDo(result -> {
                WebResponse<List<EventResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(false, response.getStatus());
            assertNotNull(response.getErrors());
        });
    }
}
