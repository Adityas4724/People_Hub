// ============================================================
// 1. PACKAGE DECLARATION
// Defines the package where the AdminSeeder class is located.
// ============================================================
package com.peoplehub.config;


// ============================================================
// 2. IMPORTS
// Imports required classes for admin creation, database access,
// Spring Boot configuration and password encryption.
// ============================================================
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.peoplehub.model.Role;
import com.peoplehub.model.User;
import com.peoplehub.repository.UserRepository;


// ============================================================
// 3. CONFIGURATION CLASS
// Marks this class as a Spring configuration class.
// ============================================================
@Configuration
public class AdminSeeder {

    // ========================================================
    // 4. ADMIN ACCOUNT INITIALIZATION
    // Runs automatically when the Spring Boot application starts.
    // Reads admin credentials from environment variables.
    // ========================================================
    @Bean
    CommandLineRunner seedAdmin(
            UserRepository users,
            PasswordEncoder encoder,
            @Value("${ADMIN_USERNAME:}") String username,
            @Value("${ADMIN_EMAIL:}") String email,
            @Value("${ADMIN_PASSWORD:}") String password) {

        return args -> {

            // =================================================
            // 5. VALIDATE ADMIN CREDENTIALS
            // Checks whether all required admin details exist.
            // =================================================
            if (username.isBlank() || email.isBlank() || password.isBlank()) {

                System.out.println(
                    "PeopleHub: admin bootstrap skipped; set ADMIN_USERNAME, ADMIN_EMAIL and ADMIN_PASSWORD."
                );

                return;
            }

            // =================================================
            // 6. CHECK AND CREATE ADMIN ACCOUNT
            // Checks that username and email are not already
            // registered before creating the admin account.
            // =================================================
            if (!users.existsByUsername(username) && !users.existsByEmail(email)) {

                // Encrypts the password and saves the admin user.
                users.save(
                    new User(
                        username,
                        email,
                        encoder.encode(password),
                        Role.ADMIN
                    )
                );
            }
        };
    }
}