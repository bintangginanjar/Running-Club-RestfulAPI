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

import com.runclub.restful.api.model.EventResponse;
import com.runclub.restful.api.model.RegisterEventRequest;
import com.runclub.restful.api.model.UpdateEventRequest;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.service.EventService;

@RestController
public class EventController {
    
    @Autowired
    EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping(
        path = "/api/clubs/{clubId}/events",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventResponse> create(Authentication authentication,
                                                @PathVariable("clubId") String clubid,
                                                @RequestBody RegisterEventRequest request) {
        
        request.setClubId(clubid);

        EventResponse response = eventService.create(authentication, request, clubid);

        return WebResponse.<EventResponse>builder()
                                        .status(true)
                                        .messages("Event registration success")
                                        .data(response)
                                        .build();
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/clubs/{clubId}/events/{eventId}",      
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventResponse> get(Authentication authentication, 
                                            @PathVariable("clubId") String clubId,
                                            @PathVariable("eventId") String eventId) {

        EventResponse response = eventService.get(authentication, clubId, eventId);

        return WebResponse.<EventResponse>builder()
                                        .status(true)
                                        .messages("Event fetching success")
                                        .data(response)
                                        .build();
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping(
        path = "/api/clubs/{clubId}/events/{eventId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventResponse> delete(Authentication authentication, 
                                            @PathVariable("clubId") String clubId,
                                            @PathVariable("eventId") String eventId,                                            
                                            @RequestBody UpdateEventRequest request) {

        EventResponse response = eventService.update(authentication, clubId, eventId, request);

        return WebResponse.<EventResponse>builder()
                                        .status(true)
                                        .messages("Event update success")
                                        .data(response)
                                        .build();
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(
        path = "/api/clubs/{clubId}/events/{eventId}",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Authentication authentication, 
                                            @PathVariable("clubId") String clubId,
                                            @PathVariable("eventId") String eventId) {

        eventService.delete(authentication, clubId, eventId);

        return WebResponse.<String>builder()
                                        .status(true)
                                        .messages("Event delete success")                                        
                                        .build();
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/events",      
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EventResponse>> getAll(Authentication authentication) {

        List<EventResponse> response = eventService.getAll();

        return WebResponse.<List<EventResponse>>builder()
                                        .status(true)
                                        .messages("Event fetching success")
                                        .data(response)
                                        .build();
    }

    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/events/list",      
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EventResponse>> list(Authentication authentication) {

        List<EventResponse> response = eventService.list(authentication);

        return WebResponse.<List<EventResponse>>builder()
                                        .status(true)
                                        .messages("Event fetching success")
                                        .data(response)
                                        .build();
    }
}
