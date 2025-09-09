package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Course;
import net.javaguides.sms.entity.Enrollment;
import net.javaguides.sms.entity.Student;
import net.javaguides.sms.repository.CourseRepository;
import net.javaguides.sms.repository.EnrollmentRepository;
import net.javaguides.sms.repository.StudentRepository;
import net.javaguides.sms.service.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    @Override
    public Enrollment enroll(Long studentId, Long courseId) {
        return enrollmentRepository.findByStudent_IdAndCourse_Id(studentId, courseId)
                .orElseGet(() -> {
                    Student student = studentRepository.findById(studentId).orElseThrow();
                    Course course = courseRepository.findById(courseId).orElseThrow();
                    Enrollment e = new Enrollment(student, course);
                    e.setStatus("ENROLLED");
                    return enrollmentRepository.save(e);
                });
    }

    @Transactional
    @Override
    public void unenroll(Long enrollmentId) {
        enrollmentRepository.deleteById(enrollmentId);
    }

    @Override
    public List<Enrollment> findByStudent(Long studentId) {
        return enrollmentRepository.findByStudent_Id(studentId);
    }

    @Override
    public List<Enrollment> findByCourse(Long courseId) {
        return enrollmentRepository.findByCourse_Id(courseId);
    }

    @Override
    public Page<Enrollment> findAll(Pageable pageable) {
        return enrollmentRepository.findAll(pageable);
    }
}
