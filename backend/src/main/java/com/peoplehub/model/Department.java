// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.model;


// ============================================================
// 2. IMPORTS
// ============================================================
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


// ============================================================
// 3. ENTITY CONFIGURATION
// ============================================================
@Entity
public class Department {

    // ========================================================
    // 4. PRIMARY KEY
    // ========================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================================
    // 5. DEPARTMENT NAME
    // ========================================================
    @Column(nullable = false, unique = true)
    private String name;

    // ========================================================
    // 6. DEPARTMENT DESCRIPTION
    // ========================================================
    private String description;

    // ========================================================
    // 7. PROTECTED DEFAULT CONSTRUCTOR
    // ========================================================
    protected Department() {}

    // ========================================================
    // 8. PARAMETERIZED CONSTRUCTOR
    // ========================================================
    public Department(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // ========================================================
    // 9. GETTER METHODS
    // ========================================================
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // ========================================================
    // 10. SETTER METHODS
    // ========================================================
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}