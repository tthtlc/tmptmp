package com.ntuc.ntuclms.config;

import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!memberRepository.existsByUsername("admin")) {
            Member admin = new Member();
            admin.setName("System Administrator");
            admin.setUsername("admin");
            admin.setEmail("admin@library.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(Member.Role.ADMIN);
            admin.setMembershipStatus(Member.MembershipStatus.ACTIVE);
            admin.setRegistrationDate(LocalDate.now());
            
            memberRepository.save(admin);
            System.out.println("Default admin user created: username=admin, password=password");
        }
    }
}

