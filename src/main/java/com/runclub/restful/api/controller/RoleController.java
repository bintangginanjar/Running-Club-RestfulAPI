package com.runclub.restful.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.runclub.restful.api.model.RoleResponse;
import com.runclub.restful.api.model.WebResponse;
import com.runclub.restful.api.service.RoleService;

@RestController
public class RoleController {

    @Autowired
    RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(
        path = "/api/roles/list",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<RoleResponse>> list(Authentication authentication) {
        List<RoleResponse> response = roleService.list(authentication);

        return WebResponse.<List<RoleResponse>>builder()
                                            .status(true)
                                            .messages("User fetching success")
                                            .data(response)
                                            .build();
    }
}
