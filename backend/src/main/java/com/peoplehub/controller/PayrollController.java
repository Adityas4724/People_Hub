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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.peoplehub.dto.PayrollRequest;
import com.peoplehub.model.Payroll;
import com.peoplehub.repository.EmployeeRepository;
import com.peoplehub.repository.PayrollRepository;

import jakarta.validation.Valid;


// ============================================================
// 3. REST CONTROLLER CONFIGURATION
// ============================================================
@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    // ========================================================
    // 4. REPOSITORY DECLARATIONS
    // ========================================================
    private final PayrollRepository payroll;
    private final EmployeeRepository employees;

    // ========================================================
    // 5. CONSTRUCTOR INJECTION
    // ========================================================
    public PayrollController(PayrollRepository p, EmployeeRepository e) {
        payroll = p;
        employees = e;
    }

    // ========================================================
    // 6. VIEW PAYROLL API (ADMIN AND EMPLOYEE)
    // ========================================================
    @GetMapping
    public List<Payroll> mineOrAll(Principal p) {

        boolean admin = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return admin
                ? payroll.findAll()
                : payroll.findByEmployeeUserUsernameOrderByPayMonthDesc(p.getName());
    }

    // ========================================================
    // 7. CREATE PAYROLL API (ADMIN ONLY)
    // ========================================================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Payroll create(@Valid @RequestBody PayrollRequest r) {

        var employee = employees.findByIdAndUser_ActiveTrue(r.employeeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Active employee not found"));

        // Validate that deductions do not exceed gross pay
        if (r.deductions().compareTo(r.basic().add(r.allowances())) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Deductions cannot exceed gross pay");
        }

        return payroll.save(new Payroll(
                employee,
                r.payMonth(),
                r.basic(),
                r.allowances(),
                r.deductions(),
                r.paidOn()));
    }
}