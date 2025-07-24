// src/main/java/com/ntuc/ntuclms/service/MemberService.java
package com.ntuc.ntuclms.service;

import com.ntuc.ntuclms.dto.RegisterRequest;
import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Member getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return memberRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public boolean existsByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member createMember(RegisterRequest registerRequest) {
        Member member = new Member();
        member.setName(registerRequest.getName());
        member.setUsername(registerRequest.getUsername());
        member.setEmail(registerRequest.getEmail());
        member.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        member.setRole("ADMIN".equals(registerRequest.getRole()) ? Member.Role.ADMIN : Member.Role.USER);
        member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        member.setRegistrationDate(LocalDate.now());
        return memberRepository.save(member);
    }

    public Member getProfile() {
        return getCurrentMember();
    }

    public Member updateProfile(Member updatedMember) {
        Member current = getCurrentMember();
        
        // Check if username is being changed and if it already exists
        if (!current.getUsername().equals(updatedMember.getUsername()) && 
            existsByUsername(updatedMember.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email is being changed and if it already exists
        if (!current.getEmail().equals(updatedMember.getEmail()) && 
            existsByEmail(updatedMember.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        current.setName(updatedMember.getName());
        current.setEmail(updatedMember.getEmail());
        current.setUsername(updatedMember.getUsername());
        
        if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
            current.setPassword(passwordEncoder.encode(updatedMember.getPassword()));
        }
        
        return memberRepository.save(current);
    }

    public boolean canBorrow(Member member) {
        // Check if member has active membership
        if (member.getMembershipStatus() != Member.MembershipStatus.ACTIVE) {
            return false;
        }
        
        // Check if member has overdue books
        long overdueLoans = member.getLoans().stream()
            .filter(loan -> loan.getReturnDate() == null && loan.isOverdue())
            .count();
        
        if (overdueLoans > 0) {
            return false;
        }
        
        // Check if member has 3 or more active loans
        long activeLoans = member.getLoans().stream()
            .filter(loan -> loan.getReturnDate() == null)
            .count();
        
        return activeLoans < 3;
    }

    // Admin methods
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member addMember(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRegistrationDate(LocalDate.now());
        if (member.getRole() == null) {
            member.setRole(Member.Role.USER);
        }
        if (member.getMembershipStatus() == null) {
            member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        }
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public Member updateMember(Long id, Member updatedMember) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found"));
        
        member.setName(updatedMember.getName());
        member.setUsername(updatedMember.getUsername());
        member.setEmail(updatedMember.getEmail());
        member.setRole(updatedMember.getRole());
        member.setMembershipStatus(updatedMember.getMembershipStatus());
        
        if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(updatedMember.getPassword()));
        }
        
        return memberRepository.save(member);
    }

    public Member renewMembership(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setRegistrationDate(LocalDate.now());
        member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        return memberRepository.save(member);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public List<Member> searchByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
}
