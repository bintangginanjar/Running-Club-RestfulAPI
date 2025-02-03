package com.runclub.restful.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.runclub.restful.api.model.LoginUserRequest;
import com.runclub.restful.api.model.TokenResponse;
import com.runclub.restful.api.security.CustomUserDetailService;
import com.runclub.restful.api.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    ValidationService validationService;

    public AuthService(AuthenticationManager authenticationManager, 
                        ValidationService validationService,
                        JwtUtil jwtUtil) {        
        this.authenticationManager = authenticationManager;
        this.validationService = validationService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        try {
            Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword())
                                );
                                            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            
            String token = jwtUtil.generateToken(authentication);

            return TokenResponse.builder().token(token).roles(roles).build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password");
        }        
    }

    @Transactional
    public void logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        
        if (authentication != null && authentication.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            /*
            String token = request.getHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                throw new BadCredentialsException("Invalid token");
            }
            */
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username not found");
        }

    }
}
