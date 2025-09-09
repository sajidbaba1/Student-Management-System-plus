package net.javaguides.sms.service;

import net.javaguides.sms.entity.Book;
import net.javaguides.sms.entity.BookIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LibraryService {
    // Book management
    Book saveBook(Book book);
    List<Book> getAllBooks();
    List<Book> searchBooks(String search);
    List<Book> getAvailableBooks();
    List<String> getAllCategories();
    Book getBookById(Long id);
    void deleteBook(Long id);
    Page<Book> getAllBooks(Pageable pageable);
    
    // Book issue management
    BookIssue issueBook(Long bookId, Long studentId, LocalDate dueDate, String issuedBy);
    BookIssue returnBook(Long issueId, String returnedBy, Double fine);
    List<BookIssue> getIssuesByStudent(Long studentId);
    List<BookIssue> getCurrentlyIssuedBooks();
    List<BookIssue> getOverdueBooks();
    BookIssue getIssueById(Long id);
    Page<BookIssue> getAllIssues(Pageable pageable);
}
