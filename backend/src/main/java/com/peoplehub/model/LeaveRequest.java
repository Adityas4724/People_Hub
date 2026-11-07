// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.model;


// ============================================================
// 2. IMPORTS
// ============================================================
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;


// ============================================================
// 3. ENTITY CONFIGURATION
// ============================================================
@Entity
public class LeaveRequest {

    // ========================================================
    // 4. PRIMARY KEY
    // ========================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================================
    // 5. EMPLOYEE RELATIONSHIP
    // ========================================================
    @ManyToOne(optional = false)
    private Employee employee;

    // ========================================================
    // 6. LEAVE DATE DETAILS
    // ========================================================
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // ========================================================
    // 7. LEAVE REASON
    // ========================================================
    @Column(nullable = false, length = 1000)
    private String reason;

    // ========================================================
    // 8. LEAVE STATUS
    // ========================================================
    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING;

    // ========================================================
    // 9. APPLICATION CREATION DATE AND TIME
    // ========================================================
    private LocalDateTime createdAt = LocalDateTime.now();

    // ========================================================
    // 10. PROTECTED DEFAULT CONSTRUCTOR
    // ========================================================
    protected LeaveRequest() {}

    // ========================================================
    // 11. PARAMETERIZED CONSTRUCTOR
    // ========================================================
    public LeaveRequest(
            Employee e,
            LocalDate s,
            LocalDate end,
            String reason) {

        this.employee = e;
        this.startDate = s;
        this.endDate = end;
        this.reason = reason;
    }

    // ========================================================
    // 12. GETTER METHODS
    // ========================================================
    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    // ========================================================
    // 13. SETTER METHOD
    // ========================================================
    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
}