package com.runclub.restful.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.runclub.restful.api.model.LoginUserRequest;
import com.runclub.restful.api.model.TokenResponse;
import com.runclub.restful.api.security.JwtGenerator;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    ValidationService validationService;

    public AuthService(AuthenticationManager authenticationManager, 
                        ValidationService validationService,
                        JwtGenerator jwtGenerator) {        
        this.authenticationManager = authenticationManager;
        this.validationService = validationService;
        this.jwtGenerator = jwtGenerator;
    }

    public TokenResponse login(LoginUserRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                            request.getUsername(), request.getPassword())
                                        );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);

            return TokenResponse.builder().token(token).build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password");
        }
        
    }
}
