// ======================================================
// 1. Package Declaration
// ======================================================
package com.peoplehub.repository;


// ======================================================
// 2. Import Statements
// ======================================================
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehub.model.Attendance;


// ======================================================
// 3. AttendanceRepository Interface
// ======================================================
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // ==================================================
    // 4. Find Attendance by Employee ID and Work Date
    // ==================================================
    Optional<Attendance> findByEmployeeIdAndWorkDate(
            Long employeeId,
            java.time.LocalDate date
    );


    // ==================================================
    // 5. Find Employee Attendance by Username
    // ==================================================
    List<Attendance> findByEmployeeUserUsernameOrderByWorkDateDesc(
            String username
    );


    // ==================================================
    // 6. Find Active Employees' Attendance Records
    // ==================================================
    List<Attendance> findByEmployee_User_ActiveTrueOrderByWorkDateDesc();
}