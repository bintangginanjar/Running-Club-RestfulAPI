package com.runclub.restful.api.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.runclub.restful.api.entity.ClubEntity;
import com.runclub.restful.api.entity.EventEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.mapper.ResponseMapper;
import com.runclub.restful.api.model.EventResponse;
import com.runclub.restful.api.model.RegisterEventRequest;
import com.runclub.restful.api.model.UpdateEventRequest;
import com.runclub.restful.api.repository.ClubRepository;
import com.runclub.restful.api.repository.EventRepository;
import com.runclub.restful.api.repository.UserRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    public EventService(EventRepository eventRepository, 
                        UserRepository userRepository,
                        ValidationService validationService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Transactional
    public EventResponse create(Authentication authentication, RegisterEventRequest request, String strClubId) {
        Integer clubId = 0;

        try {
            clubId = Integer.parseInt(strClubId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        validationService.validate(request);

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ClubEntity club = clubRepository.findFirstByCreatedByAndId(user, clubId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));
        
        if (eventRepository.findByName(request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event already registered");
        }

        EventEntity event = new EventEntity();
        event.setName(request.getName());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setType(request.getType());
        event.setPhotoUrl(request.getPhotoUrl());
        event.setCreatedBy(user);
        event.setClub(club);

        eventRepository.save(event);

        return ResponseMapper.ToEventResponseMapper(event);
    }

    @Transactional(readOnly = true)
    public EventResponse get(Authentication authentication, String strClubId, String strEventId) {
        Integer clubId = 0;
        Integer eventId = 0;

        try {
            clubId = Integer.parseInt(strClubId);
            eventId = Integer.parseInt(strEventId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ClubEntity club = clubRepository.findFirstByCreatedByAndId(user, clubId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));

        EventEntity event = eventRepository.findFirstByClubAndId(club, eventId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")); 

        return ResponseMapper.ToEventResponseMapper(event);
    }

    @Transactional
    public EventResponse update(Authentication authentication, String strClubId, String strEventId, UpdateEventRequest request) {
        Integer clubId = 0;
        Integer eventId = 0;

        try {
            clubId = Integer.parseInt(strClubId);
            eventId = Integer.parseInt(strEventId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ClubEntity club = clubRepository.findFirstByCreatedByAndId(user, clubId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));

        EventEntity event = eventRepository.findFirstByClubAndId(club, eventId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")); 
        
        if (Objects.nonNull(request.getName())) {
            event.setName(request.getName());
        }

        if (Objects.nonNull(request.getStartTime())) {
            event.setStartTime(request.getStartTime());
        }

        if (Objects.nonNull(request.getEndTime())) {
            event.setEndTime(request.getEndTime());
        }

        if (Objects.nonNull(request.getType())) {
            event.setType(request.getType());
        }

        if (Objects.nonNull(request.getPhotoUrl())) {
            event.setPhotoUrl(request.getPhotoUrl());
        }

        eventRepository.save(event);

        return ResponseMapper.ToEventResponseMapper(event);
    }

    @Transactional
    public void delete(Authentication authentication, String strClubId, String strEventId) {
        Integer clubId = 0;
        Integer eventId = 0;

        try {
            clubId = Integer.parseInt(strClubId);
            eventId = Integer.parseInt(strEventId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ClubEntity club = clubRepository.findFirstByCreatedByAndId(user, clubId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));

        EventEntity event = eventRepository.findFirstByClubAndId(club, eventId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")); 

        eventRepository.delete(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAll() {
        List<EventEntity> events = eventRepository.findAll();

        return ResponseMapper.ToEventResponseListMapper(events);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> list(Authentication authentication) {

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<EventEntity> events = eventRepository.findByCreatedBy(user);

        return ResponseMapper.ToEventResponseListMapper(events);
    }
}
