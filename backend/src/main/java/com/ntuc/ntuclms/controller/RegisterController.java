// New src/main/java/com/ntuc/ntuclms/controller/RegisterController.java
package com.ntuc.ntuclms.controller;

import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegisterController {

    private final MemberService memberService;

    public RegisterController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody Member member) {
        member.setRole(Member.Role.USER);
        return ResponseEntity.ok(memberService.addMember(member));
    }
}
