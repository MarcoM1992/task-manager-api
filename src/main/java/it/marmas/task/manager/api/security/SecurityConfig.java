package it.marmas.task.manager.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import it.marmas.task.manager.api.auth.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @PreAuthorize and @PostAuthorize annotations in services
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService; // Loads users from DB for authentication

    // Constructor-based injection for clarity
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * JWT utility bean
     * Handles generating and validating JWT tokens
     */
    @Bean
    JwtUtil getJwtUtil() {
        return new JwtUtil();
    }

    /**
     * JWT authentication filter bean
     * Intercepts requests to validate JWT tokens
     */
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(getJwtUtil(), userDetailsService);
    }

    /**
     * Password encoder bean
     * Uses BCrypt for secure password hashing
     */
    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager bean
     * Required for manual authentication (e.g., login endpoint)
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Security filter chain configuration
     * Defines which endpoints are public and which require authentication/roles
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Disable CSRF because this is a stateless REST API
                .csrf(csrf -> csrf.disable())

                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/imgs/**").permitAll() // Static resources open
                        .requestMatchers("/auth/**").permitAll() // Public endpoints (login/register)
                        .requestMatchers("/user/*", "/task/**").hasAnyRole("ADMIN", "USER") // USER or ADMIN access
                        .requestMatchers("/ADMIN/*").hasRole("ADMIN") // ADMIN-only endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // API docs open
                        .anyRequest().authenticated() // All other requests require authentication
                )

                // Handle unauthorized or forbidden access
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"You do not have permission to access this resource. " +
                                    "Contact the administrator.\"}");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized access. Missing or invalid token.\"}");
                        })
                )

                // Use stateless session management; rely only on JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Add the JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Authentication provider bean
     * Uses DAO-based authentication with the custom user details service and password encoder
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(getPasswordEncoder());
        return provider;
    }
}
