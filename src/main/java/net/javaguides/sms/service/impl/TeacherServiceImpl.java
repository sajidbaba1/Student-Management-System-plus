package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Teacher;
import net.javaguides.sms.entity.Timetable;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.exception.ResourceInUseException;
import net.javaguides.sms.repository.TeacherRepository;
import net.javaguides.sms.service.TeacherService;
import net.javaguides.sms.service.TimetableService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TimetableService timetableService;

    public TeacherServiceImpl(TeacherRepository teacherRepository, TimetableService timetableService) {
        this.teacherRepository = teacherRepository;
        this.timetableService = timetableService;
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAllByActiveTrue();
    }

    @Override
    public Teacher saveTeacher(Teacher teacher) {
        try {
            teacher.setActive(true);
            return teacherRepository.save(teacher);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("A teacher with the email '" + teacher.getEmail() + "' already exists.");
        }
    }

    @Override
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        try {
            return teacherRepository.save(teacher);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("A teacher with the email '" + teacher.getEmail() + "' already exists.");
        }
    }

    @Override
    public void deleteTeacherById(Long id) {
        List<Timetable> timetables = timetableService.findByTeacherId(id);
        if (!timetables.isEmpty()) {
            throw new ResourceInUseException("Cannot delete teacher with ID " + id + " because they are referenced in " + timetables.size() + " timetable(s).");
        }
        Teacher t = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));
        t.setActive(false);
        teacherRepository.save(t);
    }

    @Override
    public Page<Teacher> searchTeachers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return teacherRepository.searchTeachers(keyword, pageable);
        }
        return teacherRepository.findAllByActiveTrue(pageable);
    }
}