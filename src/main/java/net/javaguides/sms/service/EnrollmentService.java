package net.javaguides.sms.service;

import net.javaguides.sms.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnrollmentService {
    Enrollment enroll(Long studentId, Long courseId);
    void unenroll(Long enrollmentId);
    List<Enrollment> findByStudent(Long studentId);
    List<Enrollment> findByCourse(Long courseId);
    Page<Enrollment> findAll(Pageable pageable);
}
