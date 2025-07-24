package com.ntuc.ntuclms.service;

import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private Member testUser;
    private Member testAdmin;

    @BeforeEach
    void setUp() {
        testUser = new Member();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Member.Role.USER);
        testUser.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        testUser.setRegistrationDate(LocalDate.now());

        testAdmin = new Member();
        testAdmin.setId(2L);
        testAdmin.setName("Jane Smith");
        testAdmin.setUsername("janesmith");
        testAdmin.setEmail("jane.smith@example.com");
        testAdmin.setPassword("encodedPassword");
        testAdmin.setRole(Member.Role.ADMIN);
        testAdmin.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        testAdmin.setRegistrationDate(LocalDate.now());
    }

    @Test
    public void testAddNormalUser() {
        // Given
        Member newUser = new Member();
        newUser.setName("John Doe");
        newUser.setUsername("johndoe");
        newUser.setEmail("john.doe@example.com");
        newUser.setPassword("password123");
        newUser.setRole(Member.Role.USER);

        when(memberRepository.existsByUsername(anyString())).thenReturn(false);
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(testUser);

        // When
        Member result = memberService.addMember(newUser);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("johndoe", result.getUsername());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(Member.Role.USER, result.getRole());
        assertEquals(Member.MembershipStatus.ACTIVE, result.getMembershipStatus());
        assertNotNull(result.getRegistrationDate());

        verify(memberRepository).existsByUsername("johndoe");
        verify(memberRepository).existsByEmail("john.doe@example.com");
        verify(passwordEncoder).encode("password123");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    public void testAddAdminUser() {
        // Given
        Member newAdmin = new Member();
        newAdmin.setName("Jane Smith");
        newAdmin.setUsername("janesmith");
        newAdmin.setEmail("jane.smith@example.com");
        newAdmin.setPassword("password123");
        newAdmin.setRole(Member.Role.ADMIN);

        when(memberRepository.existsByUsername(anyString())).thenReturn(false);
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(testAdmin);

        // When
        Member result = memberService.addMember(newAdmin);

        // Then
        assertNotNull(result);
        assertEquals("Jane Smith", result.getName());
        assertEquals("janesmith", result.getUsername());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals(Member.Role.ADMIN, result.getRole());
        assertEquals(Member.MembershipStatus.ACTIVE, result.getMembershipStatus());

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    public void testGetAllMembers() {
        // Given
        List<Member> members = Arrays.asList(testUser, testAdmin);
        when(memberRepository.findAll()).thenReturn(members);

        // When
        List<Member> result = memberService.getAllMembers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
        assertEquals(Member.Role.USER, result.get(0).getRole());
        assertEquals(Member.Role.ADMIN, result.get(1).getRole());

        verify(memberRepository).findAll();
    }

    @Test
    public void testAddMemberWithDuplicateUsername() {
        // Given
        Member newUser = new Member();
        newUser.setUsername("johndoe");
        newUser.setEmail("john.doe@example.com");

        when(memberRepository.existsByUsername("johndoe")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberService.addMember(newUser);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(memberRepository).existsByUsername("johndoe");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    public void testAddMemberWithDuplicateEmail() {
        // Given
        Member newUser = new Member();
        newUser.setUsername("newuser");
        newUser.setEmail("john.doe@example.com");

        when(memberRepository.existsByUsername("newuser")).thenReturn(false);
        when(memberRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberService.addMember(newUser);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(memberRepository).existsByEmail("john.doe@example.com");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    public void testFindByUsername() {
        // Given
        when(memberRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        Optional<Member> result = memberService.findByUsername("johndoe");

        // Then
        assertTrue(result.isPresent());
        assertEquals("johndoe", result.get().getUsername());
        assertEquals("John Doe", result.get().getName());

        verify(memberRepository).findByUsername("johndoe");
    }

    @Test
    public void testDeleteMember() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        memberService.deleteMember(1L);

        // Then
        verify(memberRepository).findById(1L);
        verify(memberRepository).deleteById(1L);
    }

    @Test
    public void testDeleteNonExistentMember() {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberService.deleteMember(999L);
        });

        assertEquals("Member not found", exception.getMessage());
        verify(memberRepository).findById(999L);
        verify(memberRepository, never()).deleteById(any());
    }
}

