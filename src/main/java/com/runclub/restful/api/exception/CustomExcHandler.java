package com.runclub.restful.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.runclub.restful.api.model.WebResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class CustomExcHandler {

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> constraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> apiException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getReason())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> handlerNotFoundException(NoHandlerFoundException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> badCredentialException(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> accessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> signatureException(SignatureException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> tokenExpiredException(ExpiredJwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> credentialNotFoundException(AuthenticationCredentialsNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }    
}
