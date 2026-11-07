// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.controller;


// ============================================================
// 2. IMPORTS
// ============================================================
import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.peoplehub.dto.LeaveRequestDto;
import com.peoplehub.model.LeaveRequest;
import com.peoplehub.model.LeaveStatus;
import com.peoplehub.repository.EmployeeRepository;
import com.peoplehub.repository.LeaveRequestRepository;

import jakarta.validation.Valid;


// ============================================================
// 3. REST CONTROLLER CONFIGURATION
// ============================================================
@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    // ========================================================
    // 4. REPOSITORY DECLARATIONS
    // ========================================================
    private final LeaveRequestRepository leaves;
    private final EmployeeRepository employees;

    // ========================================================
    // 5. CONSTRUCTOR INJECTION
    // ========================================================
    public LeaveController(
            LeaveRequestRepository l,
            EmployeeRepository e) {

        leaves = l;
        employees = e;
    }

    // ========================================================
    // 6. APPLY FOR LEAVE API
    // ========================================================
    @PostMapping
    public LeaveRequest apply(
            Principal p,
            @Valid @RequestBody LeaveRequestDto r) {

        if (r.endDate().isBefore(r.startDate()))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date must be on or after start date");

        var emp = employees.findByUserUsername(p.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));

        return leaves.save(
                new LeaveRequest(
                        emp,
                        r.startDate(),
                        r.endDate(),
                        r.reason()));
    }

    // ========================================================
    // 7. VIEW LEAVE REQUESTS API (ADMIN AND EMPLOYEE)
    // ========================================================
    @GetMapping
    public List<LeaveRequest> allOrMine(Principal p) {

        boolean admin = org.springframework.security.core.context
                .SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return admin
                ? leaves.findAll()
                : leaves.findByEmployeeUserUsernameOrderByCreatedAtDesc(
                        p.getName());
    }

    // ========================================================
    // 8. APPROVE OR REJECT LEAVE API (ADMIN ONLY)
    // ========================================================
    @PatchMapping("/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public LeaveRequest decide(
            @PathVariable Long id,
            @RequestParam LeaveStatus status) {

        if (status == LeaveStatus.PENDING)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Decision must be APPROVED or REJECTED");

        var row = leaves.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leave request not found"));

        row.setStatus(status);

        return leaves.save(row);
    }
}