package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s WHERE s.active = true AND (LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Student> searchStudents(@Param("keyword") String keyword, Pageable pageable);

    Page<Student> findAllByActiveTrue(Pageable pageable);
    List<Student> findAllByActiveTrue();

    Optional<Student> findByEmail(String email);
}