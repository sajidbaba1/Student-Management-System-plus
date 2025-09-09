package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Book;
import net.javaguides.sms.entity.BookIssue;
import net.javaguides.sms.entity.Student;
import net.javaguides.sms.repository.BookRepository;
import net.javaguides.sms.repository.BookIssueRepository;
import net.javaguides.sms.repository.StudentRepository;
import net.javaguides.sms.service.LibraryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;
    private final StudentRepository studentRepository;

    public LibraryServiceImpl(BookRepository bookRepository, 
                             BookIssueRepository bookIssueRepository,
                             StudentRepository studentRepository) {
        this.bookRepository = bookRepository;
        this.bookIssueRepository = bookIssueRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> searchBooks(String search) {
        return bookRepository.searchBooks(search);
    }

    @Override
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }

    @Override
    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public BookIssue issueBook(Long bookId, Long studentId, LocalDate dueDate, String issuedBy) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        Student student = studentRepository.findById(studentId).orElseThrow();
        
        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available for issue");
        }
        
        book.issueBook();
        bookRepository.save(book);
        
        BookIssue issue = new BookIssue(book, student, dueDate, issuedBy);
        return bookIssueRepository.save(issue);
    }

    @Transactional
    @Override
    public BookIssue returnBook(Long issueId, String returnedBy, Double fine) {
        BookIssue issue = bookIssueRepository.findById(issueId).orElseThrow();
        issue.returnBook(returnedBy, fine);
        return bookIssueRepository.save(issue);
    }

    @Override
    public List<BookIssue> getIssuesByStudent(Long studentId) {
        return bookIssueRepository.findByStudent_Id(studentId);
    }

    @Override
    public List<BookIssue> getCurrentlyIssuedBooks() {
        return bookIssueRepository.findByReturnDateIsNull();
    }

    @Override
    public List<BookIssue> getOverdueBooks() {
        return bookIssueRepository.findByDueDateBeforeAndStatus(LocalDate.now(), "ISSUED");
    }

    @Override
    public BookIssue getIssueById(Long id) {
        return bookIssueRepository.findById(id).orElseThrow();
    }

    @Override
    public Page<BookIssue> getAllIssues(Pageable pageable) {
        return bookIssueRepository.findAll(pageable);
    }
}
