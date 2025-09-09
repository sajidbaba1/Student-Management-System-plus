package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Enrollment;
import net.javaguides.sms.service.EnrollmentService;
import net.javaguides.sms.service.CourseService;
import net.javaguides.sms.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentController(EnrollmentService enrollmentService, StudentService studentService, CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Enrollment> p = enrollmentService.findAll(pageable);
        model.addAttribute("enrollments", p.getContent());
        model.addAttribute("currentPage", p.getNumber());
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("totalItems", p.getTotalElements());
        return "enrollments";
    }

    @PostMapping
    public String enroll(@RequestParam Long studentId,
                         @RequestParam Long courseId,
                         RedirectAttributes ra) {
        enrollmentService.enroll(studentId, courseId);
        ra.addFlashAttribute("message", "Enrolled successfully");
        return "redirect:/enrollments";
    }

    @PostMapping("/{id}/delete")
    public String unenroll(@PathVariable Long id, RedirectAttributes ra) {
        enrollmentService.unenroll(id);
        ra.addFlashAttribute("message", "Unenrolled successfully");
        return "redirect:/enrollments";
    }
}
