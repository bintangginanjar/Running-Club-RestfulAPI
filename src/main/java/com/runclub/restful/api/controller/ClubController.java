package com.runclub.restful.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.runclub.restful.api.model.ClubResponse;
import com.runclub.restful.api.model.RegisterClubRequest;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.service.ClubService;

@RestController
public class ClubController {

    @Autowired
    private ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(
        path = "/api/clubs",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ClubResponse> create(Authentication authentication,
                                                @RequestBody RegisterClubRequest request) {
        
        ClubResponse response = clubService.create(authentication, request);

        return WebResponse.<ClubResponse>builder()
                                        .status(true)
                                        .messages("Club registration success")
                                        .data(response)
                                        .build();
    }
}
