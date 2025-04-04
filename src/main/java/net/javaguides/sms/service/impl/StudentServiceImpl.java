package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Student;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.repository.StudentRepository;
import net.javaguides.sms.service.StudentService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student saveStudent(Student student) {
        try {
            return studentRepository.save(student);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("A student with the email '" + student.getEmail() + "' already exists.");
        }
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Override
    public Student updateStudent(Student student) {
        try {
            return studentRepository.save(student);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("A student with the email '" + student.getEmail() + "' already exists.");
        }
    }

    @Override
    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Page<Student> searchStudents(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return studentRepository.searchStudents(keyword, pageable);
        }
        return studentRepository.findAll(pageable);
    }
}