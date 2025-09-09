package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Student;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public String listStudents(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "sort", defaultValue = "firstName") String sort,
                               @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        Sort sortObj = ("desc".equalsIgnoreCase(direction)) ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", studentPage.getNumber());
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        return "students";
    }

    @GetMapping("/students/export")
    public void exportStudentsCsv(HttpServletResponse response,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "sort", defaultValue = "firstName") String sort,
                                  @RequestParam(value = "direction", defaultValue = "asc") String direction) throws IOException {
        Sort sortObj = ("desc".equalsIgnoreCase(direction)) ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=students.csv");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("First Name,Last Name,Email");
            for (Student s : studentPage.getContent()) {
                String fn = s.getFirstName() != null ? s.getFirstName().replaceAll(",", " ") : "";
                String ln = s.getLastName() != null ? s.getLastName().replaceAll(",", " ") : "";
                String em = s.getEmail() != null ? s.getEmail().replaceAll(",", " ") : "";
                writer.printf("%s,%s,%s%n", fn, ln, em);
            }
        }
    }

    @GetMapping("/students/new")
    public String createStudentForm(Model model) {
        Student student = new Student();
        model.addAttribute("student", student);
        return "create_student";
    }

    @PostMapping("/students")
    public String saveStudent(@Valid @ModelAttribute("student") Student student,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "create_student";
        }
        try {
            studentService.saveStudent(student);
            return "redirect:/students";
        } catch (DuplicateResourceException e) {
            model.addAttribute("error", e.getMessage());
            return "create_student";
        }
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "edit_student";
    }

    @PostMapping("/students/{id}")
    public String updateStudent(@PathVariable Long id,
                               @Valid @ModelAttribute("student") Student student,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "edit_student";
        }
        try {
            Student existingStudent = studentService.getStudentById(id);
            existingStudent.setId(id);
            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());
            existingStudent.setEmail(student.getEmail());
            studentService.updateStudent(existingStudent);
            return "redirect:/students";
        } catch (DuplicateResourceException e) {
            model.addAttribute("error", e.getMessage());
            return "edit_student";
        }
    }

    @GetMapping("/students/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return "redirect:/students";
    }
}