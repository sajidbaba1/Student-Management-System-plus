package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourse_Id(Long courseId);
    List<Assignment> findByTeacher_Id(Long teacherId);
    List<Assignment> findByStatus(String status);
    List<Assignment> findByDueDateBetween(LocalDateTime start, LocalDateTime end);
    List<Assignment> findByDueDateBeforeAndStatus(LocalDateTime date, String status);
}
