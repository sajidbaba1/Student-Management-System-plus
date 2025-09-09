package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Grade;
import net.javaguides.sms.service.GradeService;
import net.javaguides.sms.service.StudentService;
import net.javaguides.sms.service.CourseService;
import net.javaguides.sms.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/grades")
public class GradeController {

    private final GradeService gradeService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final TeacherService teacherService;

    public GradeController(GradeService gradeService, StudentService studentService, 
                          CourseService courseService, TeacherService teacherService) {
        this.gradeService = gradeService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.teacherService = teacherService;
    }

    @GetMapping
    public String listGrades(@RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grade> grades = gradeService.getAllGrades(pageable);
        
        model.addAttribute("grades", grades.getContent());
        model.addAttribute("currentPage", grades.getNumber());
        model.addAttribute("totalPages", grades.getTotalPages());
        model.addAttribute("totalItems", grades.getTotalElements());
        return "grades";
    }

    @GetMapping("/new")
    public String newGradeForm(Model model) {
        model.addAttribute("grade", new Grade());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "create_grade";
    }

    @PostMapping
    public String saveGrade(@ModelAttribute Grade grade, RedirectAttributes ra) {
        gradeService.saveGrade(grade);
        ra.addFlashAttribute("message", "Grade saved successfully");
        return "redirect:/grades";
    }

    @GetMapping("/student/{studentId}")
    public String studentGrades(@PathVariable Long studentId, Model model) {
        model.addAttribute("grades", gradeService.getGradesByStudent(studentId));
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("gpa", gradeService.calculateGPA(studentId));
        return "student_grades";
    }

    @PostMapping("/{id}/delete")
    public String deleteGrade(@PathVariable Long id, RedirectAttributes ra) {
        gradeService.deleteGrade(id);
        ra.addFlashAttribute("message", "Grade deleted successfully");
        return "redirect:/grades";
    }
}
