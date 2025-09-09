package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    @Query("SELECT t FROM Teacher t WHERE t.active = true AND (LOWER(t.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Teacher> searchTeachers(@Param("keyword") String keyword, Pageable pageable);

    Page<Teacher> findAllByActiveTrue(Pageable pageable);
    List<Teacher> findAllByActiveTrue();

    Optional<Teacher> findByEmail(String email);
}