package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Book;
import net.javaguides.sms.entity.BookIssue;
import net.javaguides.sms.service.LibraryService;
import net.javaguides.sms.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;
    private final StudentService studentService;

    public LibraryController(LibraryService libraryService, StudentService studentService) {
        this.libraryService = libraryService;
        this.studentService = studentService;
    }

    @GetMapping
    public String libraryHome(Model model) {
        model.addAttribute("totalBooks", libraryService.getAllBooks().size());
        model.addAttribute("availableBooks", libraryService.getAvailableBooks().size());
        model.addAttribute("issuedBooks", libraryService.getCurrentlyIssuedBooks().size());
        model.addAttribute("overdueBooks", libraryService.getOverdueBooks().size());
        return "library_dashboard";
    }

    @GetMapping("/books")
    public String listBooks(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "search", required = false) String search,
                           Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books;
        
        if (search != null && !search.trim().isEmpty()) {
            books = Page.empty(); // For simplicity, convert list to page
            model.addAttribute("books", libraryService.searchBooks(search));
        } else {
            books = libraryService.getAllBooks(pageable);
            model.addAttribute("books", books.getContent());
        }
        
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("totalItems", books.getTotalElements());
        model.addAttribute("search", search);
        return "library_books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add_book";
    }

    @PostMapping("/books")
    public String saveBook(@ModelAttribute Book book, RedirectAttributes ra) {
        libraryService.saveBook(book);
        ra.addFlashAttribute("message", "Book added successfully");
        return "redirect:/library/books";
    }

    @GetMapping("/issues")
    public String listIssues(@RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookIssue> issues = libraryService.getAllIssues(pageable);
        
        model.addAttribute("issues", issues.getContent());
        model.addAttribute("currentPage", issues.getNumber());
        model.addAttribute("totalPages", issues.getTotalPages());
        model.addAttribute("totalItems", issues.getTotalElements());
        return "library_issues";
    }

    @PostMapping("/issue")
    public String issueBook(@RequestParam Long bookId,
                           @RequestParam Long studentId,
                           @RequestParam String dueDate,
                           Authentication auth,
                           RedirectAttributes ra) {
        try {
            libraryService.issueBook(bookId, studentId, LocalDate.parse(dueDate), auth.getName());
            ra.addFlashAttribute("message", "Book issued successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to issue book: " + e.getMessage());
        }
        return "redirect:/library/issues";
    }

    @PostMapping("/return/{issueId}")
    public String returnBook(@PathVariable Long issueId,
                            @RequestParam(defaultValue = "0") Double fine,
                            Authentication auth,
                            RedirectAttributes ra) {
        libraryService.returnBook(issueId, auth.getName(), fine);
        ra.addFlashAttribute("message", "Book returned successfully");
        return "redirect:/library/issues";
    }

    @GetMapping("/overdue")
    public String overdueBooks(Model model) {
        model.addAttribute("issues", libraryService.getOverdueBooks());
        return "overdue_books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes ra) {
        libraryService.deleteBook(id);
        ra.addFlashAttribute("message", "Book deleted successfully");
        return "redirect:/library/books";
    }
}
