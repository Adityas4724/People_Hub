// ======================================================
// 1. Package Declaration
// ======================================================
package com.peoplehub.repository;


// ======================================================
// 2. Import Statements
// ======================================================
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehub.model.User;


// ======================================================
// 3. UserRepository Interface
// ======================================================
public interface UserRepository extends JpaRepository<User, Long> {

    // ==================================================
    // 4. Find User by Username
    // ==================================================
    Optional<User> findByUsername(String username);


    // ==================================================
    // 5. Check Whether Username Already Exists
    // ==================================================
    boolean existsByUsername(String username);


    // ==================================================
    // 6. Check Whether Email Already Exists
    // ==================================================
    boolean existsByEmail(String email);
}