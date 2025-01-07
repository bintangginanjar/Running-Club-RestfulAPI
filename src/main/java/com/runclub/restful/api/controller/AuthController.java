package com.runclub.restful.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.runclub.restful.api.service.UserService;

@RestController
public class AuthController {

    @Autowired
    UserService userService;

}
