// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.config;

// ============================================================
// 2. IMPORTS
// ============================================================
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.peoplehub.security.JwtAuthFilter;

// ============================================================
// 3. SECURITY CONFIGURATION CLASS
// ============================================================
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // ========================================================
    // 4. JWT FILTER AND CORS CONFIGURATION
    // ========================================================
    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    // Constructor injection for JWT authentication filter
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // ========================================================
    // 5. PASSWORD ENCODER CONFIGURATION
    // ========================================================
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ========================================================
    // 6. AUTHENTICATION MANAGER CONFIGURATION
    // ========================================================
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration c)
            throws Exception {
        return c.getAuthenticationManager();
    }

    // ========================================================
    // 7. CORS CONFIGURATION
    // ========================================================
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();

        c.setAllowedOrigins(List.of(allowedOrigin));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        c.setExposedHeaders(List.of("Authorization"));
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);

        return s;
    }

    // ========================================================
    // 8. SECURITY FILTER CHAIN CONFIGURATION
    // ========================================================
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // Disable CSRF for the stateless API
            .csrf(csrf -> csrf.disable())

            // Enable CORS configuration
            .cors(cors -> {})

            // Configure stateless session management
            .sessionManagement(s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Define endpoint authorization rules
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**", "/api/health").permitAll()
                .anyRequest().authenticated())

            // Add JWT filter before the username/password filter
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class)

            .build();
    }
}