// src/main/java/com/ntuc/ntuclms/repository/BookRepository.java
package com.ntuc.ntuclms.repository;

import com.ntuc.ntuclms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByIsbnContainingIgnoreCase(String isbn);
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);
    List<Book> findByAvailableTrue();
    long countByAvailableTrue();
}
