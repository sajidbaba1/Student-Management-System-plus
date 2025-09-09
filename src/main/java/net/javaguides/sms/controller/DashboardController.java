package net.javaguides.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import net.javaguides.sms.repository.StudentRepository;
import net.javaguides.sms.repository.TeacherRepository;
import net.javaguides.sms.repository.CourseRepository;
import net.javaguides.sms.repository.TimetableRepository;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final TimetableRepository timetableRepository;

    public DashboardController(StudentRepository studentRepository,
                               TeacherRepository teacherRepository,
                               CourseRepository courseRepository,
                               TimetableRepository timetableRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.timetableRepository = timetableRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", studentRepository.count());
        model.addAttribute("totalTeachers", teacherRepository.count());
        model.addAttribute("totalCourses", courseRepository.count());
        model.addAttribute("totalTimetables", timetableRepository.count());
        return "dashboard";
    }
}
