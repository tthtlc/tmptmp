package com.ntuc.ntuclms.controller;

import com.ntuc.ntuclms.entity.Book;
import com.ntuc.ntuclms.entity.Loan;
import com.ntuc.ntuclms.entity.Member;
import com.ntuc.ntuclms.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    private Loan loan1;
    private Loan loan2;
    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setUsername("johndoe");
        member.setEmail("john.doe@example.com");
        member.setRole(Member.Role.USER);

        book = new Book();
        book.setId(1L);
        book.setIsbn("978-0134685991");
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setAvailable(false);

        loan1 = new Loan();
        loan1.setId(1L);
        loan1.setMember(member);
        loan1.setBook(book);
        loan1.setLoanDate(LocalDate.now().minusDays(5));
        loan1.setDueDate(LocalDate.now().plusDays(9));
        loan1.setReturnDate(null);
        loan1.setOverdue(false);

        loan2 = new Loan();
        loan2.setId(2L);
        loan2.setMember(member);
        loan2.setBook(book);
        loan2.setLoanDate(LocalDate.now().minusDays(20));
        loan2.setDueDate(LocalDate.now().minusDays(6));
        loan2.setReturnDate(null);
        loan2.setOverdue(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testListAllBookLoans() throws Exception {
        // Given
        List<Loan> loans = Arrays.asList(loan1, loan2);
        when(loanService.getAllLoans()).thenReturn(loans);

        // When & Then
        mockMvc.perform(get("/api/admin/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].member.name").value("John Doe"))
                .andExpect(jsonPath("$[0].book.title").value("Effective Java"))
                .andExpect(jsonPath("$[0].overdue").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].overdue").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOverdueLoans() throws Exception {
        // Given
        List<Loan> overdueLoans = Arrays.asList(loan2);
        when(loanService.getOverdueLoans()).thenReturn(overdueLoans);

        // When & Then
        mockMvc.perform(get("/api/admin/loans/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].overdue").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSearchLoansByMemberName() throws Exception {
        // Given
        List<Loan> memberLoans = Arrays.asList(loan1, loan2);
        when(loanService.searchLoansByMemberName("John Doe")).thenReturn(memberLoans);

        // When & Then
        mockMvc.perform(get("/api/admin/loans/search")
                .param("name", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].member.name").value("John Doe"))
                .andExpect(jsonPath("$[1].member.name").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUnauthorizedAccessToAdminLoansEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/loans"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEmptyLoansList() throws Exception {
        // Given
        when(loanService.getAllLoans()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/admin/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

