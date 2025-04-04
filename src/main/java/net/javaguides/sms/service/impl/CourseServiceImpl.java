package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Course;
import net.javaguides.sms.entity.Timetable;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.exception.ResourceInUseException;
import net.javaguides.sms.repository.CourseRepository;
import net.javaguides.sms.service.CourseService;
import net.javaguides.sms.service.TimetableService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TimetableService timetableService;

    public CourseServiceImpl(CourseRepository courseRepository, TimetableService timetableService) {
        this.courseRepository = courseRepository;
        this.timetableService = timetableService;
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course saveCourse(Course course) {
        try {
            return courseRepository.save(course);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("A course with the name '" + course.getCourseName() + "' already exists.");
        }
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public Course updateCourse(Course course) {
        try {
            return courseRepository.save(course);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("A course with the name '" + course.getCourseName() + "' already exists.");
        }
    }

    @Override
    public void deleteCourseById(Long id) {
        List<Timetable> timetables = timetableService.findByCourseId(id);
        if (!timetables.isEmpty()) {
            throw new ResourceInUseException("Cannot delete course with ID " + id + " because it is referenced in " + timetables.size() + " timetable(s).");
        }
        courseRepository.deleteById(id);
    }

    @Override
    public Page<Course> searchCourses(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return courseRepository.searchCourses(keyword, pageable);
        }
        return courseRepository.findAll(pageable);
    }
}