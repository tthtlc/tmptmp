// src/main/java/com/ntuc/ntuclms/controller/MemberController.java
package com.ntuc.ntuclms.controller;

import com.ntuc.ntuclms.entity.Loan;
import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.service.LoanService;
import com.ntuc.ntuclms.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MemberController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private LoanService loanService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        try {
            Member member = memberService.getCurrentMember();
            List<Loan> currentLoans = loanService.getCurrentLoans();
            List<Loan> history = loanService.getLoanHistory();
            double totalFines = loanService.calculateTotalFines();
            
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("member", member);
            dashboardData.put("currentLoans", currentLoans);
            dashboardData.put("loanHistory", history);
            dashboardData.put("totalFines", totalFines);
            dashboardData.put("canBorrow", memberService.canBorrow(member));
            
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading dashboard: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Member member = memberService.getProfile();
            // Remove password from response
            member.setPassword(null);
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading profile: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody Member updatedMember) {
        try {
            Member member = memberService.updateProfile(updatedMember);
            // Remove password from response
            member.setPassword(null);
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    @GetMapping("/loans")
    public ResponseEntity<?> getCurrentLoans() {
        try {
            List<Loan> loans = loanService.getCurrentLoans();
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading current loans: " + e.getMessage());
        }
    }

    @GetMapping("/loans/history")
    public ResponseEntity<?> getLoanHistory() {
        try {
            List<Loan> history = loanService.getLoanHistory();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading loan history: " + e.getMessage());
        }
    }

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId) {
        try {
            Loan loan = loanService.borrowBook(bookId);
            return ResponseEntity.ok(Map.of(
                "message", "Book borrowed successfully",
                "loan", loan
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error borrowing book: " + e.getMessage());
        }
    }

    @PostMapping("/renew/{loanId}")
    public ResponseEntity<?> renewLoan(@PathVariable Long loanId) {
        try {
            Loan loan = loanService.renewLoan(loanId);
            return ResponseEntity.ok(Map.of(
                "message", "Loan renewed successfully",
                "loan", loan
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error renewing loan: " + e.getMessage());
        }
    }

    @PostMapping("/return/{loanId}")
    public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
        try {
            Loan loan = loanService.returnBook(loanId);
            return ResponseEntity.ok(Map.of(
                "message", "Book returned successfully",
                "loan", loan,
                "fine", loan.getFine()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error returning book: " + e.getMessage());
        }
    }

    @GetMapping("/fines")
    public ResponseEntity<?> getTotalFines() {
        try {
            double totalFines = loanService.calculateTotalFines();
            return ResponseEntity.ok(Map.of("totalFines", totalFines));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error calculating fines: " + e.getMessage());
        }
    }

    @GetMapping("/eligibility")
    public ResponseEntity<?> checkBorrowEligibility() {
        try {
            Member member = memberService.getCurrentMember();
            boolean canBorrow = memberService.canBorrow(member);
            return ResponseEntity.ok(Map.of("canBorrow", canBorrow));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking eligibility: " + e.getMessage());
        }
    }
}
