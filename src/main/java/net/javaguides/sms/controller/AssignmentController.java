package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Assignment;
import net.javaguides.sms.entity.AssignmentSubmission;
import net.javaguides.sms.service.AssignmentService;
import net.javaguides.sms.service.CourseService;
import net.javaguides.sms.service.TeacherService;
import net.javaguides.sms.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    public AssignmentController(AssignmentService assignmentService, CourseService courseService,
                               TeacherService teacherService, StudentService studentService) {
        this.assignmentService = assignmentService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    @GetMapping
    public String listAssignments(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Assignment> assignments = assignmentService.getAllAssignments(pageable);
        
        model.addAttribute("assignments", assignments.getContent());
        model.addAttribute("currentPage", assignments.getNumber());
        model.addAttribute("totalPages", assignments.getTotalPages());
        model.addAttribute("totalItems", assignments.getTotalElements());
        return "assignments";
    }

    @GetMapping("/new")
    public String newAssignmentForm(Model model) {
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "create_assignment";
    }

    @PostMapping
    public String saveAssignment(@ModelAttribute Assignment assignment, RedirectAttributes ra) {
        assignmentService.saveAssignment(assignment);
        ra.addFlashAttribute("message", "Assignment created successfully");
        return "redirect:/assignments";
    }

    @GetMapping("/{id}/submissions")
    public String viewSubmissions(@PathVariable Long id, Model model) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", assignmentService.getSubmissionsByAssignment(id));
        return "assignment_submissions";
    }

    @GetMapping("/{id}/submit")
    public String submitForm(@PathVariable Long id, Model model) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", new AssignmentSubmission());
        return "submit_assignment";
    }

    @PostMapping("/{id}/submit")
    public String submitAssignment(@PathVariable Long id,
                                  @ModelAttribute AssignmentSubmission submission,
                                  Authentication auth,
                                  RedirectAttributes ra) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        // In a real app, you'd get the student by the authenticated user
        submission.setAssignment(assignment);
        assignmentService.submitAssignment(submission);
        ra.addFlashAttribute("message", "Assignment submitted successfully");
        return "redirect:/assignments";
    }

    @PostMapping("/submissions/{id}/grade")
    public String gradeSubmission(@PathVariable Long id,
                                 @RequestParam Double score,
                                 @RequestParam String feedback,
                                 @RequestParam Long teacherId,
                                 RedirectAttributes ra) {
        assignmentService.gradeSubmission(id, score, feedback, teacherId);
        ra.addFlashAttribute("message", "Submission graded successfully");
        return "redirect:/assignments";
    }

    @PostMapping("/{id}/delete")
    public String deleteAssignment(@PathVariable Long id, RedirectAttributes ra) {
        assignmentService.deleteAssignment(id);
        ra.addFlashAttribute("message", "Assignment deleted successfully");
        return "redirect:/assignments";
    }
}
