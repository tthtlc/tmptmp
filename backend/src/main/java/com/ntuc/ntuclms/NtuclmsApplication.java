// Updated src/main/java/com/ntuc/ntuclms/NtuclmsApplication.java
package com.ntuc.ntuclms;

import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.repository.MemberRepository;
import com.ntuc.ntuclms.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class NtuclmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NtuclmsApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (memberRepository.findByUsername("admin").isEmpty()) {
				Member admin = new Member();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setEmail("admin@example.com");
				admin.setName("Admin");
				admin.setRole(Member.Role.ADMIN);
				admin.setRegistrationDate(LocalDate.now());
				memberRepository.save(admin);
			}
		};
	}
}
