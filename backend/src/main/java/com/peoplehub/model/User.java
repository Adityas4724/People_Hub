// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.model;


// ============================================================
// 2. IMPORTS
// ============================================================
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


// ============================================================
// 3. ENTITY AND TABLE CONFIGURATION
// ============================================================
@Entity
@Table(name = "users")
public class User {

    // ========================================================
    // 4. PRIMARY KEY
    // ========================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================================
    // 5. USER ACCOUNT DETAILS
    // ========================================================
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // ========================================================
    // 6. USER ROLE CONFIGURATION
    // ========================================================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ========================================================
    // 7. ACCOUNT STATUS AND CREATION DATE
    // ========================================================
    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ========================================================
    // 8. PROTECTED DEFAULT CONSTRUCTOR
    // ========================================================
    protected User() {}

    // ========================================================
    // 9. PARAMETERIZED CONSTRUCTOR
    // ========================================================
    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = true;
    }

    // ========================================================
    // 10. GETTER METHODS
    // ========================================================
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // ========================================================
    // 11. PASSWORD PROTECTION
    // ========================================================
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    // ========================================================
    // 12. ROLE AND ACCOUNT STATUS GETTERS
    // ========================================================
    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    // ========================================================
    // 13. ACCOUNT STATUS UPDATE
    // ========================================================
    public void setActive(boolean active) {
        this.active = active;
    }
}