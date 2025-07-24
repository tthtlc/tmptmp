package com.ntuc.ntuclms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.service.MemberService;
import com.ntuc.ntuclms.service.BookService;
import com.ntuc.ntuclms.service.LoanService;
import com.ntuc.ntuclms.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testUser;
    private Member testAdmin;

    @BeforeEach
    void setUp() {
        testUser = new Member();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole(Member.Role.USER);
        testUser.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        testUser.setRegistrationDate(LocalDate.now());

        testAdmin = new Member();
        testAdmin.setId(2L);
        testAdmin.setName("Jane Smith");
        testAdmin.setUsername("janesmith");
        testAdmin.setEmail("jane.smith@example.com");
        testAdmin.setRole(Member.Role.ADMIN);
        testAdmin.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        testAdmin.setRegistrationDate(LocalDate.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddNormalUser() throws Exception {
        // Given
        Member newUser = new Member();
        newUser.setName("John Doe");
        newUser.setUsername("johndoe");
        newUser.setEmail("john.doe@example.com");
        newUser.setPassword("password123");
        newUser.setRole(Member.Role.USER);

        when(memberService.addMember(any(Member.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/admin/members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddAdminUser() throws Exception {
        // Given
        Member newAdmin = new Member();
        newAdmin.setName("Jane Smith");
        newAdmin.setUsername("janesmith");
        newAdmin.setEmail("jane.smith@example.com");
        newAdmin.setPassword("password123");
        newAdmin.setRole(Member.Role.ADMIN);

        when(memberService.addMember(any(Member.class))).thenReturn(testAdmin);

        // When & Then
        mockMvc.perform(post("/api/admin/members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.username").value("janesmith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testListAllUsers() throws Exception {
        // Given
        List<Member> members = Arrays.asList(testUser, testAdmin);
        when(memberService.getAllMembers()).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/admin/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddMemberWithInvalidData() throws Exception {
        // Given
        Member invalidMember = new Member();
        // Missing required fields

        when(memberService.addMember(any(Member.class)))
                .thenThrow(new RuntimeException("Validation failed"));

        // When & Then
        mockMvc.perform(post("/api/admin/members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUnauthorizedAccessToAdminEndpoint() throws Exception {
        // Given
        Member newUser = new Member();
        newUser.setName("Test User");
        newUser.setUsername("testuser");
        newUser.setEmail("test@example.com");

        // When & Then
        mockMvc.perform(post("/api/admin/members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isForbidden());
    }
}

