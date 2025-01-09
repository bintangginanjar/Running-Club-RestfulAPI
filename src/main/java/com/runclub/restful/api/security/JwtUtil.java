package com.runclub.restful.api.security;

import java.security.Key;
import java.util.Date;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currDate = new Date();
        Date expDate = new Date(currDate.getTime() + SecurityConstants.JWTexpiration);

        String token = Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(currDate)
                        .setExpiration(expDate)
                        .signWith(key, SignatureAlgorithm.HS512)
                        .compact();

        return token;
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        return claims.getSubject();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

                return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT was exprired or incorrect", e.fillInStackTrace());
        }
    }
}
