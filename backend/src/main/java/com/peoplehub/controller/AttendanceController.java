// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.controller;


// ============================================================
// 2. IMPORTS
// ============================================================
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.peoplehub.model.Attendance;
import com.peoplehub.repository.AttendanceRepository;
import com.peoplehub.repository.EmployeeRepository;


// ============================================================
// 3. REST CONTROLLER CONFIGURATION
// ============================================================
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    // ========================================================
    // 4. REPOSITORY DECLARATIONS
    // ========================================================
    private final AttendanceRepository attendance;
    private final EmployeeRepository employees;

    // ========================================================
    // 5. CONSTRUCTOR INJECTION
    // ========================================================
    public AttendanceController(
            AttendanceRepository a,
            EmployeeRepository e) {
        attendance = a;
        employees = e;
    }

    // ========================================================
    // 6. EMPLOYEE CHECK-IN API
    // ========================================================
    @PostMapping("/check-in")
    public Attendance checkIn(Principal p) {

        var emp = employees.findByUserUsername(p.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));

        var row = attendance.findByEmployeeIdAndWorkDate(
                emp.getId(), LocalDate.now())
                .orElseGet(() -> new Attendance(emp, LocalDate.now()));

        if (row.getCheckIn() != null)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Already checked in today");

        row.setCheckIn(LocalDateTime.now());

        return attendance.save(row);
    }

    // ========================================================
    // 7. EMPLOYEE CHECK-OUT API
    // ========================================================
    @PostMapping("/check-out")
    public Attendance checkOut(Principal p) {

        var emp = employees.findByUserUsername(p.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));

        var row = attendance.findByEmployeeIdAndWorkDate(
                emp.getId(), LocalDate.now())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Check in first"));

        if (row.getCheckOut() != null)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Already checked out");

        row.setCheckOut(LocalDateTime.now());

        return attendance.save(row);
    }

    // ========================================================
    // 8. VIEW ATTENDANCE API (ADMIN AND EMPLOYEE)
    // ========================================================
    @GetMapping
    public List<Attendance> allOrMine(Principal p) {

        boolean admin = org.springframework.security.core.context
                .SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return admin
                ? attendance.findByEmployee_User_ActiveTrueOrderByWorkDateDesc()
                : attendance.findByEmployeeUserUsernameOrderByWorkDateDesc(
                        p.getName());
    }

    // ========================================================
    // 9. VIEW MY ATTENDANCE API
    // ========================================================
    @GetMapping("/me")
    public List<Attendance> mine(Principal p) {
        return attendance.findByEmployeeUserUsernameOrderByWorkDateDesc(
                p.getName());
    }
}