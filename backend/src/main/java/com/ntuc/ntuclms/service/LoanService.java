// src/main/java/com/ntuc/ntuclms/service/LoanService.java
package com.ntuc.ntuclms.service;

import com.ntuc.ntuclms.entity.Book;
import com.ntuc.ntuclms.entity.Loan;
import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.repository.BookRepository;
import com.ntuc.ntuclms.repository.LoanRepository;
import com.ntuc.ntuclms.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private MemberService memberService;

    private Member getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return memberRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Loan borrowBook(Long bookId) {
        Member member = getCurrentMember();
        
        if (!memberService.canBorrow(member)) {
            throw new RuntimeException("Cannot borrow: Check membership status, active loans, or overdue books");
        }
        
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (!book.isAvailable() || loanRepository.existsByBookAndReturnDateIsNull(book)) {
            throw new RuntimeException("Book is not available");
        }
        
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBook(book);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14)); // 14 days loan period
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        
        // Update book availability
        book.setAvailable(false);
        bookRepository.save(book);
        
        return loanRepository.save(loan);
    }

    public Loan renewLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));
        
        if (!loan.getMember().equals(getCurrentMember())) {
            throw new RuntimeException("Not authorized to renew this loan");
        }
        
        if (loan.getReturnDate() != null) {
            throw new RuntimeException("Loan already returned");
        }
        
        if (!loan.canRenew()) {
            throw new RuntimeException("Loan cannot be renewed (overdue, max renewals reached, or not renewable)");
        }
        
        loan.renewLoan();
        return loanRepository.save(loan);
    }

    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));
        
        if (!loan.getMember().equals(getCurrentMember())) {
            throw new RuntimeException("Not authorized to return this loan");
        }
        
        if (loan.getReturnDate() != null) {
            throw new RuntimeException("Loan already returned");
        }
        
        LocalDate now = LocalDate.now();
        loan.setReturnDate(now);
        loan.setStatus(Loan.LoanStatus.RETURNED);
        
        // Calculate fine if overdue
        loan.calculateFine();
        
        // Update book availability
        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);
        
        return loanRepository.save(loan);
    }

    public List<Loan> getCurrentLoans() {
        Member member = getCurrentMember();
        return loanRepository.findByMemberAndReturnDateIsNull(member);
    }

    public List<Loan> getLoanHistory() {
        Member member = getCurrentMember();
        return loanRepository.findByMemberOrderByBorrowDateDesc(member);
    }

    public double calculateTotalFines() {
        Member member = getCurrentMember();
        return member.getLoans().stream()
            .mapToDouble(Loan::getFine)
            .sum();
    }

    // Admin methods
    public Loan createLoan(Long memberId, String isbn) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
        
        if (!memberService.canBorrow(member)) {
            throw new RuntimeException("Member cannot borrow: Check membership status, active loans, or overdue books");
        }
        
        Book book = bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (!book.isAvailable() || loanRepository.existsByBookAndReturnDateIsNull(book)) {
            throw new RuntimeException("Book is not available");
        }
        
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBook(book);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        
        // Update book availability
        book.setAvailable(false);
        bookRepository.save(book);
        
        return loanRepository.save(loan);
    }

    public Loan extendLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));
        
        if (loan.getReturnDate() != null) {
            throw new RuntimeException("Loan already returned");
        }
        
        if (loan.isOverdue()) {
            throw new RuntimeException("Cannot extend overdue loan");
        }
        
        loan.setDueDate(loan.getDueDate().plusDays(14));
        return loanRepository.save(loan);
    }

    public void deleteLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));
        
        // If loan is active, make book available again
        if (loan.getReturnDate() == null) {
            Book book = loan.getBook();
            book.setAvailable(true);
            bookRepository.save(book);
        }
        
        loanRepository.deleteById(loanId);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAllByOrderByBorrowDateDesc();
    }

    public List<Loan> searchLoansByMemberName(String name) {
        return loanRepository.findByMemberNameContainingIgnoreCaseOrderByBorrowDateDesc(name);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findByReturnDateIsNullAndDueDateBefore(LocalDate.now());
    }

    public void updateOverdueLoansStatus() {
        List<Loan> overdueLoans = getOverdueLoans();
        for (Loan loan : overdueLoans) {
            loan.calculateFine();
            loanRepository.save(loan);
        }
    }
}
