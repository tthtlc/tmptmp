// src/main/java/com/ntuc/ntuclms/entity/Loan.java
package com.ntuc.ntuclms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDate borrowDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private double fine = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;

    private boolean renewable = true;

    private int renewalCount = 0;

    private static final int MAX_RENEWALS = 2;
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 1.0;

    public enum LoanStatus {
        ACTIVE, RETURNED, OVERDUE
    }

    @PrePersist
    protected void onCreate() {
        if (borrowDate == null) {
            borrowDate = LocalDate.now();
        }
        if (dueDate == null) {
            dueDate = borrowDate.plusDays(LOAN_PERIOD_DAYS);
        }
    }

    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }

    public boolean canRenew() {
        return renewable && renewalCount < MAX_RENEWALS && !isOverdue() && returnDate == null;
    }

    public void calculateFine() {
        if (isOverdue()) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            this.fine = overdueDays * FINE_PER_DAY;
            this.status = LoanStatus.OVERDUE;
        }
    }

    public void renewLoan() {
        if (canRenew()) {
            this.renewalCount++;
            this.dueDate = this.dueDate.plusDays(LOAN_PERIOD_DAYS);
            if (this.renewalCount >= MAX_RENEWALS) {
                this.renewable = false;
            }
        }
    }
}
