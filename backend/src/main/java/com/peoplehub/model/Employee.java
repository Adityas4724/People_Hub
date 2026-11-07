// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.model;


// ============================================================
// 2. IMPORTS
// ============================================================
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;


// ============================================================
// 3. ENTITY CONFIGURATION
// ============================================================
@Entity
public class Employee {

    // ========================================================
    // 4. PRIMARY KEY
    // ========================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================================
    // 5. EMPLOYEE BASIC DETAILS
    // ========================================================
    @Column(nullable = false, unique = true)
    private String employeeCode;

    @Column(nullable = false)
    private String fullName;

    private String designation;
    private String phone;
    private LocalDate joiningDate;

    // ========================================================
    // 6. USER ACCOUNT RELATIONSHIP
    // ========================================================
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // ========================================================
    // 7. DEPARTMENT RELATIONSHIP
    // ========================================================
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // ========================================================
    // 8. PROTECTED DEFAULT CONSTRUCTOR
    // ========================================================
    protected Employee() {}

    // ========================================================
    // 9. PARAMETERIZED CONSTRUCTOR
    // ========================================================
    public Employee(
            String code,
            String name,
            String designation,
            String phone,
            LocalDate joiningDate,
            User user,
            Department department) {

        this.employeeCode = code;
        this.fullName = name;
        this.designation = designation;
        this.phone = phone;
        this.joiningDate = joiningDate;
        this.user = user;
        this.department = department;
    }

    // ========================================================
    // 10. GETTER METHODS
    // ========================================================
    public Long getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDesignation() {
        return designation;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public User getUser() {
        return user;
    }

    public Department getDepartment() {
        return department;
    }

    // ========================================================
    // 11. UPDATE EMPLOYEE DETAILS
    // ========================================================
    public void updateDetails(
            String fullName,
            String designation,
            String phone,
            LocalDate joiningDate,
            Department department) {

        this.fullName = fullName;
        this.designation = designation;
        this.phone = phone;
        this.joiningDate = joiningDate;
        this.department = department;
    }
}