// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.model;


// ============================================================
// 2. IMPORTS
// ============================================================
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;


// ============================================================
// 3. ENTITY CONFIGURATION
// ============================================================
@Entity
public class Payroll {

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
    // 6. PAYROLL MONTH
    // ========================================================
    @Column(nullable = false)
    private String payMonth;

    // ========================================================
    // 7. SALARY COMPONENTS
    // ========================================================
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basic;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal allowances;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deductions;

    // ========================================================
    // 8. NET PAY
    // ========================================================
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netPay;

    // ========================================================
    // 9. PAYMENT DATE
    // ========================================================
    private LocalDate paidOn;

    // ========================================================
    // 10. PROTECTED DEFAULT CONSTRUCTOR
    // ========================================================
    protected Payroll() {}

    // ========================================================
    // 11. PARAMETERIZED CONSTRUCTOR
    // ========================================================
    public Payroll(
            Employee e,
            String month,
            BigDecimal basic,
            BigDecimal allowances,
            BigDecimal deductions,
            LocalDate paidOn) {

        this.employee = e;
        this.payMonth = month;
        this.basic = basic;
        this.allowances = allowances;
        this.deductions = deductions;

        this.netPay = basic.add(allowances).subtract(deductions);

        this.paidOn = paidOn;
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

    public String getPayMonth() {
        return payMonth;
    }

    public BigDecimal getBasic() {
        return basic;
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public BigDecimal getNetPay() {
        return netPay;
    }

    public LocalDate getPaidOn() {
        return paidOn;
    }
}