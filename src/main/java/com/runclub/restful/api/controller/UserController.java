package com.runclub.restful.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.runclub.restful.api.model.RegisterUserRequest;
import com.runclub.restful.api.model.UpdateUserRequest;
import com.runclub.restful.api.model.UserResponse;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
        path = "/api/users",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> create(@RequestBody RegisterUserRequest request) {
        UserResponse response = userService.register(request);

        return WebResponse.<UserResponse>builder()
                                        .status(true)
                                        .messages("User registration success")
                                        .data(response)
                                        .build();      
    }
    
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping(
        path = "/api/users/current",        
        produces = MediaType.APPLICATION_JSON_VALUE
    )    
    public WebResponse<UserResponse> get(Authentication authentication) {
        UserResponse response = userService.get(authentication);

        return WebResponse.<UserResponse>builder()
                                            .status(true)
                                            .messages("User fetching success")
                                            .data(response)
                                            .build();
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PatchMapping(
        path = "/api/users/current",
        consumes = MediaType.APPLICATION_JSON_VALUE,  
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(Authentication authentication, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(authentication, request);

        return WebResponse.<UserResponse>builder()
                                            .status(true)
                                            .messages("User update success")
                                            .data(response)
                                            .build();
    }
}
