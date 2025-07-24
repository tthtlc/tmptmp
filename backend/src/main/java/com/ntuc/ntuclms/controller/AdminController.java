// src/main/java/com/ntuc/ntuclms/controller/AdminController.java
package com.ntuc.ntuclms.controller;

import com.ntuc.ntuclms.entity.Book;
import com.ntuc.ntuclms.entity.Loan;
import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.service.BookService;
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
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private LoanService loanService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        try {
            List<Member> allMembers = memberService.getAllMembers();
            List<Book> allBooks = bookService.getAllBooks();
            List<Loan> allLoans = loanService.getAllLoans();
            List<Loan> overdueLoans = loanService.getOverdueLoans();
            
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("totalMembers", allMembers.size());
            dashboardData.put("totalBooks", allBooks.size());
            dashboardData.put("totalLoans", allLoans.size());
            dashboardData.put("overdueLoans", overdueLoans.size());
            dashboardData.put("recentLoans", allLoans.stream().limit(10).toList());
            
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading admin dashboard: " + e.getMessage());
        }
    }

    // Members management
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers() {
        try {
            List<Member> members = memberService.getAllMembers();
            // Remove passwords from response
            members.forEach(member -> member.setPassword(null));
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading members: " + e.getMessage());
        }
    }

    @PostMapping("/members")
    public ResponseEntity<?> addMember(@Valid @RequestBody Member member) {
        try {
            Member savedMember = memberService.addMember(member);
            // Remove password from response
            savedMember.setPassword(null);
            return ResponseEntity.ok(savedMember);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding member: " + e.getMessage());
        }
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok(Map.of("message", "Member deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting member: " + e.getMessage());
        }
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        try {
            Member updatedMember = memberService.updateMember(id, member);
            // Remove password from response
            updatedMember.setPassword(null);
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating member: " + e.getMessage());
        }
    }

    @PutMapping("/members/{id}/renew")
    public ResponseEntity<?> renewMembership(@PathVariable Long id) {
        try {
            Member renewedMember = memberService.renewMembership(id);
            // Remove password from response
            renewedMember.setPassword(null);
            return ResponseEntity.ok(Map.of(
                "message", "Membership renewed successfully",
                "member", renewedMember
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error renewing membership: " + e.getMessage());
        }
    }

    @GetMapping("/members/search")
    public ResponseEntity<?> searchMembers(@RequestParam String name) {
        try {
            List<Member> members = memberService.searchByName(name);
            // Remove passwords from response
            members.forEach(member -> member.setPassword(null));
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching members: " + e.getMessage());
        }
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<?> getMember(@PathVariable Long id) {
        try {
            Member member = memberService.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
            // Remove password from response
            member.setPassword(null);
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading member: " + e.getMessage());
        }
    }

    // Books management
    @PostMapping("/books")
    public ResponseEntity<?> addBook(@Valid @RequestBody Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            return ResponseEntity.ok(savedBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding book: " + e.getMessage());
        }
    }

    @GetMapping("/books")
    public ResponseEntity<?> getAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading books: " + e.getMessage());
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        try {
            Book updatedBook = bookService.updateBook(id, book);
            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating book: " + e.getMessage());
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting book: " + e.getMessage());
        }
    }

    @GetMapping("/books/search")
    public ResponseEntity<?> searchBooks(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) String author,
                                        @RequestParam(required = false) String isbn) {
        try {
            List<Book> books = bookService.searchBooks(title, author, isbn);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching books: " + e.getMessage());
        }
    }

    // Loans management
    @PostMapping("/loans")
    public ResponseEntity<?> createLoan(@RequestBody Map<String, Object> request) {
        try {
            Long memberId = Long.valueOf(request.get("memberId").toString());
            String isbn = (String) request.get("isbn");
            Loan loan = loanService.createLoan(memberId, isbn);
            return ResponseEntity.ok(Map.of(
                "message", "Loan created successfully",
                "loan", loan
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating loan: " + e.getMessage());
        }
    }

    @PutMapping("/loans/{id}/extend")
    public ResponseEntity<?> extendLoan(@PathVariable Long id) {
        try {
            Loan loan = loanService.extendLoan(id);
            return ResponseEntity.ok(Map.of(
                "message", "Loan extended successfully",
                "loan", loan
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error extending loan: " + e.getMessage());
        }
    }

    @DeleteMapping("/loans/{id}")
    public ResponseEntity<?> deleteLoan(@PathVariable Long id) {
        try {
            loanService.deleteLoan(id);
            return ResponseEntity.ok(Map.of("message", "Loan deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting loan: " + e.getMessage());
        }
    }

    @GetMapping("/loans")
    public ResponseEntity<?> getAllLoans() {
        try {
            List<Loan> loans = loanService.getAllLoans();
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading loans: " + e.getMessage());
        }
    }

    @GetMapping("/loans/search")
    public ResponseEntity<?> searchLoansByMemberName(@RequestParam String name) {
        try {
            List<Loan> loans = loanService.searchLoansByMemberName(name);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching loans: " + e.getMessage());
        }
    }

    @GetMapping("/loans/overdue")
    public ResponseEntity<?> getOverdueLoans() {
        try {
            List<Loan> overdueLoans = loanService.getOverdueLoans();
            return ResponseEntity.ok(overdueLoans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading overdue loans: " + e.getMessage());
        }
    }

    @PostMapping("/loans/update-overdue")
    public ResponseEntity<?> updateOverdueLoansStatus() {
        try {
            loanService.updateOverdueLoansStatus();
            return ResponseEntity.ok(Map.of("message", "Overdue loans status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating overdue loans: " + e.getMessage());
        }
    }

    // Statistics and reports
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            List<Member> allMembers = memberService.getAllMembers();
            List<Book> allBooks = bookService.getAllBooks();
            List<Loan> allLoans = loanService.getAllLoans();
            List<Loan> overdueLoans = loanService.getOverdueLoans();
            
            long activeMembers = allMembers.stream()
                .filter(member -> member.getMembershipStatus() == Member.MembershipStatus.ACTIVE)
                .count();
            
            long availableBooks = allBooks.stream()
                .filter(Book::isAvailable)
                .count();
            
            long activeLoans = allLoans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .count();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalMembers", allMembers.size());
            statistics.put("activeMembers", activeMembers);
            statistics.put("totalBooks", allBooks.size());
            statistics.put("availableBooks", availableBooks);
            statistics.put("totalLoans", allLoans.size());
            statistics.put("activeLoans", activeLoans);
            statistics.put("overdueLoans", overdueLoans.size());
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading statistics: " + e.getMessage());
        }
    }
}
