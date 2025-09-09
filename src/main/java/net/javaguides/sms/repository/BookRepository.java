package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByCategory(String category);
    List<Book> findByIsbn(String isbn);
    List<Book> findByAvailableCopiesGreaterThan(Integer copies);
    
    @Query("SELECT DISTINCT b.category FROM Book b ORDER BY b.category")
    List<String> findAllCategories();
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:search% OR b.author LIKE %:search% OR b.isbn LIKE %:search%")
    List<Book> searchBooks(@Param("search") String search);
}
