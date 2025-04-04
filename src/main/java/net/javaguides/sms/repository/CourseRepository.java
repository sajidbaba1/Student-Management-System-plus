package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Course> searchCourses(@Param("keyword") String keyword, Pageable pageable);

    Page<Course> findAll(Pageable pageable);

    Optional<Course> findByCourseName(String courseName);
}