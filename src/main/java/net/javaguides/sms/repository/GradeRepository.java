package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent_Id(Long studentId);
    List<Grade> findByCourse_Id(Long courseId);
    List<Grade> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    
    @Query("SELECT AVG(g.score / g.maxScore * 100) FROM Grade g WHERE g.student.id = :studentId")
    Double findAveragePercentageByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT AVG(g.score / g.maxScore * 100) FROM Grade g WHERE g.student.id = :studentId AND g.course.id = :courseId")
    Double findAveragePercentageByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
