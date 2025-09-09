package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Grade;
import net.javaguides.sms.repository.GradeRepository;
import net.javaguides.sms.service.GradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    public GradeServiceImpl(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    public List<Grade> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudent_Id(studentId);
    }

    @Override
    public List<Grade> getGradesByCourse(Long courseId) {
        return gradeRepository.findByCourse_Id(courseId);
    }

    @Override
    public List<Grade> getGradesByStudentAndCourse(Long studentId, Long courseId) {
        return gradeRepository.findByStudent_IdAndCourse_Id(studentId, courseId);
    }

    @Override
    public Double calculateGPA(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudent_Id(studentId);
        if (grades.isEmpty()) return 0.0;
        
        double totalPoints = grades.stream()
                .mapToDouble(Grade::getGpaPoint)
                .sum();
        return totalPoints / grades.size();
    }

    @Override
    public Double calculateCourseAverage(Long studentId, Long courseId) {
        Double avg = gradeRepository.findAveragePercentageByStudentIdAndCourseId(studentId, courseId);
        return avg != null ? avg : 0.0;
    }

    @Override
    public Page<Grade> getAllGrades(Pageable pageable) {
        return gradeRepository.findAll(pageable);
    }

    @Override
    public void deleteGrade(Long gradeId) {
        gradeRepository.deleteById(gradeId);
    }
}
