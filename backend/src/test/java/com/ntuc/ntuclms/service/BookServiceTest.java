package com.ntuc.ntuclms.service;

import com.ntuc.ntuclms.entity.Book;
import com.ntuc.ntuclms.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
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
    public void testAddBook() {
        // Given
        Book newBook = new Book();
        newBook.setIsbn("978-0134685991");
        newBook.setTitle("Effective Java");
        newBook.setAuthor("Joshua Bloch");

        when(bookRepository.existsByIsbn("978-0134685991")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        // When
        Book result = bookService.addBook(newBook);

        // Then
        assertNotNull(result);
        assertEquals("978-0134685991", result.getIsbn());
        assertEquals("Effective Java", result.getTitle());
        assertEquals("Joshua Bloch", result.getAuthor());
        assertTrue(result.isAvailable());

        verify(bookRepository).existsByIsbn("978-0134685991");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    public void testAddBookWithDuplicateIsbn() {
        // Given
        Book newBook = new Book();
        newBook.setIsbn("978-0134685991");
        newBook.setTitle("Effective Java");
        newBook.setAuthor("Joshua Bloch");

        when(bookRepository.existsByIsbn("978-0134685991")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.addBook(newBook);
        });

        assertEquals("Book with ISBN 978-0134685991 already exists", exception.getMessage());
        verify(bookRepository).existsByIsbn("978-0134685991");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testGetAllBooks() {
        // Given
        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findAll()).thenReturn(books);

        // When
        List<Book> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());
        assertEquals("Effective C++", result.get(1).getTitle());
        assertTrue(result.get(0).isAvailable());
        assertFalse(result.get(1).isAvailable());

        verify(bookRepository).findAll();
    }

    @Test
    public void testGetAvailableBooks() {
        // Given
        List<Book> availableBooks = Arrays.asList(book1);
        when(bookRepository.findByAvailableTrue()).thenReturn(availableBooks);

        // When
        List<Book> result = bookService.getAvailableBooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());
        assertTrue(result.get(0).isAvailable());

        verify(bookRepository).findByAvailableTrue();
    }

    @Test
    public void testSearchBooksByTitle() {
        // Given
        List<Book> searchResults = Arrays.asList(book1);
        when(bookRepository.findByTitleContainingIgnoreCase("Effective Java")).thenReturn(searchResults);

        // When
        List<Book> result = bookService.searchBooks("Effective Java", null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());

        verify(bookRepository).findByTitleContainingIgnoreCase("Effective Java");
    }

    @Test
    public void testSearchBooksByAuthor() {
        // Given
        List<Book> searchResults = Arrays.asList(book1);
        when(bookRepository.findByAuthorContainingIgnoreCase("Joshua Bloch")).thenReturn(searchResults);

        // When
        List<Book> result = bookService.searchBooks(null, "Joshua Bloch", null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Joshua Bloch", result.get(0).getAuthor());

        verify(bookRepository).findByAuthorContainingIgnoreCase("Joshua Bloch");
    }

    @Test
    public void testSearchBooksByIsbn() {
        // Given
        List<Book> searchResults = Arrays.asList(book1);
        when(bookRepository.findByIsbnContainingIgnoreCase("978-0134685991")).thenReturn(searchResults);

        // When
        List<Book> result = bookService.searchBooks(null, null, "978-0134685991");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("978-0134685991", result.get(0).getIsbn());

        verify(bookRepository).findByIsbnContainingIgnoreCase("978-0134685991");
    }

    @Test
    public void testSearchBooksWithTitleAndAuthor() {
        // Given
        List<Book> searchResults = Arrays.asList(book1);
        when(bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("Effective", "Joshua"))
                .thenReturn(searchResults);

        // When
        List<Book> result = bookService.searchBooks("Effective", "Joshua", null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());
        assertEquals("Joshua Bloch", result.get(0).getAuthor());

        verify(bookRepository).findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("Effective", "Joshua");
    }

    @Test
    public void testUpdateBook() {
        // Given
        Book updatedBook = new Book();
        updatedBook.setIsbn("978-0134685991");
        updatedBook.setTitle("Effective Java 3rd Edition");
        updatedBook.setAuthor("Joshua Bloch");
        updatedBook.setAvailable(true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        // When
        Book result = bookService.updateBook(1L, updatedBook);

        // Then
        assertNotNull(result);
        assertEquals("Effective Java 3rd Edition", result.getTitle());

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    public void testDeleteAvailableBook() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        // When
        bookService.deleteBook(1L);

        // Then
        verify(bookRepository).findById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    public void testDeleteUnavailableBook() {
        // Given
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.deleteBook(2L);
        });

        assertEquals("Cannot delete book with active loans", exception.getMessage());
        verify(bookRepository).findById(2L);
        verify(bookRepository, never()).deleteById(any());
    }
}

