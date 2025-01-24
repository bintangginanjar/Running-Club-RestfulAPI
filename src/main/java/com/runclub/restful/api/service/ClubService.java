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
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.mapper.ResponseMapper;
import com.runclub.restful.api.model.ClubResponse;
import com.runclub.restful.api.model.RegisterClubRequest;
import com.runclub.restful.api.model.UpdateClubRequest;
import com.runclub.restful.api.repository.ClubRepository;
import com.runclub.restful.api.repository.UserRepository;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    public ClubService(ClubRepository clubRepository, 
                        UserRepository userRepository, 
                        ValidationService validationService) {
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Transactional
    public ClubResponse create(Authentication authentication, RegisterClubRequest request) {
        validationService.validate(request);

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (clubRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Club already registered");
        }

        ClubEntity club = new ClubEntity();
        club.setTitle(request.getTitle());
        club.setPhotoUrl(request.getPhotoUrl());
        club.setContent(request.getContent());
        club.setCreatedBy(user);
        clubRepository.save(club);

        return ResponseMapper.ToClubResponseMapper(club);
    }

    @Transactional(readOnly = true)
    public ClubResponse get(Authentication authentication, String strClubId) {
        Integer clubId = 0;

        try {
            clubId = Integer.parseInt(strClubId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ClubEntity club = clubRepository.findFirstByCreatedByAndId(user, clubId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));

        return ResponseMapper.ToClubResponseMapper(club);
    }

    @Transactional
    public ClubResponse update(Authentication authentication, String strClubId, UpdateClubRequest request) {
        Integer clubId = 0;

        try {
            clubId = Integer.parseInt(strClubId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ClubEntity club = clubRepository.findFirstByCreatedByAndId(user, clubId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));

        if (Objects.nonNull(request.getTitle())) {
            club.setTitle(request.getTitle());
        }

        if (Objects.nonNull(request.getContent())) {
            club.setContent(request.getContent());
        }

        if (Objects.nonNull(request.getPhotoUrl())) {
            club.setPhotoUrl(request.getPhotoUrl());
        }

        clubRepository.save(club);

        return ResponseMapper.ToClubResponseMapper(club);
    }

    @Transactional
    public void deletes(Authentication authentication, String strClubId) {
        Integer clubId = 0;

        try {
            clubId = Integer.parseInt(strClubId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ClubEntity club = clubRepository.findFirstByCreatedByAndId(user, clubId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));

        clubRepository.delete(club);
    }

    @Transactional(readOnly = true)
    public List<ClubResponse> getAll() {        
        List<ClubEntity> clubs = clubRepository.findAll();

        return ResponseMapper.ToClubResponseListMapper(clubs);
    }

    @Transactional(readOnly = true)
    public List<ClubResponse> list(Authentication authentication) {
    
        UserEntity user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<ClubEntity> clubs = clubRepository.findAllByCreatedBy(user);

        return ResponseMapper.ToClubResponseListMapper(clubs);
    }
}
