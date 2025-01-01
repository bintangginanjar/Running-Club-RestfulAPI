package com.runclub.restful.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.runclub.restful.api.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

}
