// src/main/java/com/ntuc/ntuclms/repository/LoanRepository.java
package com.ntuc.ntuclms.repository;

import com.ntuc.ntuclms.entity.Book;
import com.ntuc.ntuclms.entity.Loan;
import com.ntuc.ntuclms.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMember(Member member);
    List<Loan> findByMemberOrderByBorrowDateDesc(Member member);
    List<Loan> findByMemberAndReturnDateIsNull(Member member);
    List<Loan> findAllByOrderByBorrowDateDesc();
    List<Loan> findByReturnDateIsNullAndDueDateBefore(LocalDate date);
    boolean existsByMemberAndReturnDateIsNullAndDueDateBefore(Member member, LocalDate date);
    boolean existsByBookAndReturnDateIsNull(Book book);
    long countByMemberAndReturnDateIsNull(Member member);
    List<Loan> findByMemberNameContainingIgnoreCaseOrderByBorrowDateDesc(String name);
}
