package com.runclub.restful.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.runclub.restful.api.entity.ClubEntity;
import com.runclub.restful.api.entity.UserEntity;
import com.runclub.restful.api.mapper.GeneralResponseMapper;
import com.runclub.restful.api.model.ClubResponse;
import com.runclub.restful.api.model.RegisterClubRequest;
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

    public ClubService(ClubRepository clubRepository, ValidationService validationService) {
        this.clubRepository = clubRepository;
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

        return GeneralResponseMapper.ToClubResponseMapper(club);
    }
    

}
