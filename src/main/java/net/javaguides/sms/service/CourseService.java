package net.javaguides.sms.service;

import net.javaguides.sms.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course saveCourse(Course course);
    Course getCourseById(Long id);
    Course updateCourse(Course course);
    void deleteCourseById(Long id);
    Page<Course> searchCourses(String keyword, Pageable pageable);
}