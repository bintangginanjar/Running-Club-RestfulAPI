package com.runclub.restful.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private CustomUserDetailService userDetailService;
    private JwtAuthEntryPoint authEntryPoint;
    
    public SecurityConfig(CustomUserDetailService userDetailService, JwtAuthEntryPoint authEntryPoint) {
        this.userDetailService = userDetailService;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest()
                    .authenticated())
            .exceptionHandling(excHand -> excHand.accessDeniedPage("/error/403"))
            .httpBasic(httpbc -> httpbc.authenticationEntryPoint(authEntryPoint))
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(withDefaults());

        return http.build();
    }

    /*
    @Bean
    public UserDetailsService users() {
        UserDetails admin = User.builder()
                            .username("admin")
                            .password("password")
                            .roles("ADMIN")
                            .build();

        UserDetails user = User.builder()
                            .username("user")
                            .password("password")
                            .roles("USER")
                            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
