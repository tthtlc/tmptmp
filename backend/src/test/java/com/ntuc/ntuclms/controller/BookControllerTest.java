package com.ntuc.ntuclms.controller;

import com.ntuc.ntuclms.entity.Book;
import com.ntuc.ntuclms.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book();
        book1.setId(1L);
        book1.setIsbn("978-0134685991");
        book1.setTitle("Effective Java");
        book1.setAuthor("Joshua Bloch");
        book1.setAvailable(true);

        book2 = new Book();
        book2.setId(2L);
        book2.setIsbn("978-0321356680");
        book2.setTitle("Effective C++");
        book2.setAuthor("Scott Meyers");
        book2.setAvailable(false);
    }

    @Test
    public void testListAllBooks() throws Exception {
        // Given
        List<Book> books = Arrays.asList(book1, book2);
        when(bookService.getAllBooks()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Effective Java"))
                .andExpect(jsonPath("$[0].author").value("Joshua Bloch"))
                .andExpect(jsonPath("$[0].isbn").value("978-0134685991"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].title").value("Effective C++"))
                .andExpect(jsonPath("$[1].author").value("Scott Meyers"))
                .andExpect(jsonPath("$[1].isbn").value("978-0321356680"))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    public void testGetAvailableBooks() throws Exception {
        // Given
        List<Book> availableBooks = Arrays.asList(book1);
        when(bookService.getAvailableBooks()).thenReturn(availableBooks);

        // When & Then
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Effective Java"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    public void testSearchBooksByTitle() throws Exception {
        // Given
        List<Book> searchResults = Arrays.asList(book1);
        when(bookService.searchBooks("Effective Java", null, null)).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/books/search")
                .param("title", "Effective Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Effective Java"));
    }

    @Test
    public void testSearchBooksByAuthor() throws Exception {
        // Given
        List<Book> searchResults = Arrays.asList(book1);
        when(bookService.searchBooks(null, "Joshua Bloch", null)).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/books/search")
                .param("author", "Joshua Bloch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].author").value("Joshua Bloch"));
    }

    @Test
    public void testSearchBooksByIsbn() throws Exception {
        // Given
        List<Book> searchResults = Arrays.asList(book1);
        when(bookService.searchBooks(null, null, "978-0134685991")).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/books/search")
                .param("isbn", "978-0134685991"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isbn").value("978-0134685991"));
    }
}

