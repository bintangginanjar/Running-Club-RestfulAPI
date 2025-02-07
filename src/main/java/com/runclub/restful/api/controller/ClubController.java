package com.runclub.restful.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.runclub.restful.api.model.ClubResponse;
import com.runclub.restful.api.model.RegisterClubRequest;
import com.runclub.restful.api.model.UpdateClubRequest;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.service.ClubService;

@RestController
public class ClubController {

    @Autowired
    private ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
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

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/clubs/{clubId}",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ClubResponse> get(Authentication authentication, 
                                            @PathVariable("clubId") String clubId) {
        ClubResponse response = clubService.get(authentication, clubId);

        return WebResponse.<ClubResponse>builder()
                                        .status(true)
                                        .messages("Club fetching success")
                                        .data(response)
                                        .build();
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/clubs",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ClubResponse>> getAll(Authentication authentication) {
        List<ClubResponse> response = clubService.getAll();

        return WebResponse.<List<ClubResponse>>builder()
                                        .status(true)
                                        .messages("Club fetching success")
                                        .data(response)
                                        .build();
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/clubs/list",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ClubResponse>> list(Authentication authentication) {
        List<ClubResponse> response = clubService.list(authentication);

        return WebResponse.<List<ClubResponse>>builder()
                                        .status(true)
                                        .messages("Club fetching success")
                                        .data(response)
                                        .build();
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/clubs/{clubId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,      
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ClubResponse> update(Authentication authentication, 
                                            @PathVariable("clubId") String clubId,
                                            @RequestBody UpdateClubRequest request) {
        ClubResponse response = clubService.update(authentication, clubId, request);

        request.setClubId(clubId);

        return WebResponse.<ClubResponse>builder()
                                        .status(true)
                                        .messages("Club update success")
                                        .data(response)
                                        .build();
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/clubs/{clubId}",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Authentication authentication, 
                                            @PathVariable("clubId") String clubId) {
    
        clubService.deletes(authentication, clubId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Club delete success")                                        
                                        .build();
    }
}
