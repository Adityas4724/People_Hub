
package com.peoplehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehub.model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndWorkDate(
            Long employeeId,
            java.time.LocalDate date
    );

    List<Attendance> findByEmployeeUserUsernameOrderByWorkDateDesc(
            String username
    );

    List<Attendance> findByEmployee_User_ActiveTrueOrderByWorkDateDesc();
}
