// src/main/java/com/ntuc/ntuclms/repository/MemberRepository.java
package com.ntuc.ntuclms.repository;

import com.ntuc.ntuclms.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Member> findByNameContainingIgnoreCase(String name);
}
