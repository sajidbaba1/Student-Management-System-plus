package net.javaguides.sms.service;

import net.javaguides.sms.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GradeService {
    Grade saveGrade(Grade grade);
    List<Grade> getGradesByStudent(Long studentId);
    List<Grade> getGradesByCourse(Long courseId);
    List<Grade> getGradesByStudentAndCourse(Long studentId, Long courseId);
    Double calculateGPA(Long studentId);
    Double calculateCourseAverage(Long studentId, Long courseId);
    Page<Grade> getAllGrades(Pageable pageable);
    void deleteGrade(Long gradeId);
}
