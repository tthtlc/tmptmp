// src/main/java/com/ntuc/ntuclms/service/BookService.java
package com.ntuc.ntuclms.service;

import com.ntuc.ntuclms.entity.Book;
import com.ntuc.ntuclms.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book addBook(Book book) {
        // Check if book with same ISBN already exists
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public Book updateBook(Long id, Book updatedBook) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Check if ISBN is being changed and if it already exists
        if (!book.getIsbn().equals(updatedBook.getIsbn()) && 
            bookRepository.existsByIsbn(updatedBook.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + updatedBook.getIsbn() + " already exists");
        }
        
        book.setIsbn(updatedBook.getIsbn());
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setAvailable(updatedBook.isAvailable());
        
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Check if book has active loans
        if (!book.isAvailable()) {
            throw new RuntimeException("Cannot delete book with active loans");
        }
        
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String title, String author, String isbn) {
        if (isbn != null && !isbn.trim().isEmpty()) {
            return bookRepository.findByIsbnContainingIgnoreCase(isbn.trim());
        } else if (title != null && !title.trim().isEmpty() && 
                   author != null && !author.trim().isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
                title.trim(), author.trim());
        } else if (title != null && !title.trim().isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCase(title.trim());
        } else if (author != null && !author.trim().isEmpty()) {
            return bookRepository.findByAuthorContainingIgnoreCase(author.trim());
        } else {
            return getAllBooks();
        }
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableTrue();
    }

    public long countAvailableBooks() {
        return bookRepository.countByAvailableTrue();
    }

    public long countTotalBooks() {
        return bookRepository.count();
    }
}
