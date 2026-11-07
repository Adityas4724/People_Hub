// ======================================================
// 1. Package Declaration
// ======================================================
package com.peoplehub.security;


// ======================================================
// 2. Import Statements
// ======================================================
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.peoplehub.repository.UserRepository;


// ======================================================
// 3. User Details Service Class
// ======================================================
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // ==================================================
    // 4. User Repository Dependency
    // ==================================================
    private final UserRepository users;

    public UserDetailsServiceImpl(UserRepository users) {
        this.users = users;
    }


    // ==================================================
    // 5. Load User by Username
    // ==================================================
    @Override
    public UserDetails loadUserByUsername(String username) {

        // ==================================================
        // 6. Find User in Database
        // ==================================================
        var u = users.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        // ==================================================
        // 7. Build Spring Security User Details
        // ==================================================
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole().name())
                .disabled(!u.isActive())
                .build();
    }
}