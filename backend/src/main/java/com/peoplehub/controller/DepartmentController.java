// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.controller;


// ============================================================
// 2. IMPORTS
// ============================================================
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehub.dto.DepartmentRequest;
import com.peoplehub.model.Department;
import com.peoplehub.repository.DepartmentRepository;

import jakarta.validation.Valid;


// ============================================================
// 3. REST CONTROLLER CONFIGURATION
// ============================================================
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    // ========================================================
    // 4. DEPARTMENT REPOSITORY DECLARATION
    // ========================================================
    private final DepartmentRepository repo;

    // ========================================================
    // 5. CONSTRUCTOR INJECTION
    // ========================================================
    public DepartmentController(DepartmentRepository repo) {
        this.repo = repo;
    }

    // ========================================================
    // 6. GET ALL DEPARTMENTS API
    // ========================================================
    @GetMapping
    public List<Department> all() {
        return repo.findAll();
    }

    // ========================================================
    // 7. CREATE DEPARTMENT API (ADMIN ONLY)
    // ========================================================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Department create(
            @Valid @RequestBody DepartmentRequest r) {

        return repo.save(
                new Department(r.name(), r.description()));
    }

    // ========================================================
    // 8. DELETE DEPARTMENT API (ADMIN ONLY)
    // ========================================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repo.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}