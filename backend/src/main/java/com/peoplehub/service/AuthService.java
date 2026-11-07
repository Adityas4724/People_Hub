// ======================================================
// 1. Package Declaration
// ======================================================
package com.peoplehub.service;


// ======================================================
// 2. Import Statements
// ======================================================
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.peoplehub.dto.LoginRequest;
import com.peoplehub.dto.LoginResponse;
import com.peoplehub.repository.UserRepository;
import com.peoplehub.security.JwtService;


// ======================================================
// 3. Authentication Service Class
// ======================================================
@Service
public class AuthService {

    // ==================================================
    // 4. Required Dependencies
    // ==================================================
    private final AuthenticationManager manager;
    private final UserRepository users;
    private final JwtService jwt;


    // ==================================================
    // 5. Constructor Injection
    // ==================================================
    public AuthService(
            AuthenticationManager manager,
            UserRepository users,
            JwtService jwt
    ) {
        this.manager = manager;
        this.users = users;
        this.jwt = jwt;
    }


    // ==================================================
    // 6. Login Method
    // ==================================================
    public LoginResponse login(LoginRequest request) {

        // ==================================================
        // 7. Authenticate Username and Password
        // ==================================================
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );


        // ==================================================
        // 8. Retrieve User from Database
        // ==================================================
        var user = users.findByUsername(request.username())
                .orElseThrow();


        // ==================================================
        // 9. Check Account Status
        // ==================================================
        if (!user.isActive()) {
            throw new BadCredentialsException("Account is inactive");
        }


        // ==================================================
        // 10. Generate JWT and Return Login Response
        // ==================================================
        return new LoginResponse(
                jwt.generate(user.getUsername(), user.getRole().name()),
                user.getUsername(),
                user.getRole().name()
        );
    }
}