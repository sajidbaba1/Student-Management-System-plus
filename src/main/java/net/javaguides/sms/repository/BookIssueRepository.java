package net.javaguides.sms.repository;

import net.javaguides.sms.entity.BookIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByStudent_Id(Long studentId);
    List<BookIssue> findByBook_Id(Long bookId);
    List<BookIssue> findByStatus(String status);
    List<BookIssue> findByDueDateBeforeAndStatus(LocalDate date, String status);
    List<BookIssue> findByReturnDateIsNull(); // Currently issued books
    List<BookIssue> findByReturnDateIsNotNull(); // Returned books
}
