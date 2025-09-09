package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Teacher;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.exception.ResourceInUseException;
import net.javaguides.sms.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public String listTeachers(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "sort", defaultValue = "firstName") String sort,
                               @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        Sort sortObj = ("desc".equalsIgnoreCase(direction)) ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Teacher> teacherPage = teacherService.searchTeachers(keyword, pageable);
        model.addAttribute("teachers", teacherPage.getContent());
        model.addAttribute("currentPage", teacherPage.getNumber());
        model.addAttribute("totalPages", teacherPage.getTotalPages());
        model.addAttribute("totalItems", teacherPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        return "teachers";
    }

    @GetMapping("/export")
    public void exportTeachersCsv(HttpServletResponse response,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "sort", defaultValue = "firstName") String sort,
                                  @RequestParam(value = "direction", defaultValue = "asc") String direction) throws IOException {
        Sort sortObj = ("desc".equalsIgnoreCase(direction)) ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Teacher> teacherPage = teacherService.searchTeachers(keyword, pageable);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=teachers.csv");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("First Name,Last Name,Email");
            for (Teacher t : teacherPage.getContent()) {
                String fn = t.getFirstName() != null ? t.getFirstName().replaceAll(",", " ") : "";
                String ln = t.getLastName() != null ? t.getLastName().replaceAll(",", " ") : "";
                String em = t.getEmail() != null ? t.getEmail().replaceAll(",", " ") : "";
                writer.printf("%s,%s,%s%n", fn, ln, em);
            }
        }
    }

    @GetMapping("/new")
    public String createTeacherForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "create_teacher";
    }

    @PostMapping
    public String saveTeacher(@Valid @ModelAttribute("teacher") Teacher teacher,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "create_teacher";
        }
        try {
            teacherService.saveTeacher(teacher);
            return "redirect:/teachers";
        } catch (DuplicateResourceException e) {
            model.addAttribute("error", e.getMessage());
            return "create_teacher";
        }
    }

    @GetMapping("/edit/{id}")
    public String editTeacherForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "edit_teacher";
    }

    @PostMapping("/{id}")
    public String updateTeacher(@PathVariable Long id,
                              @Valid @ModelAttribute("teacher") Teacher teacher,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "edit_teacher";
        }
        try {
            Teacher existingTeacher = teacherService.getTeacherById(id);
            existingTeacher.setFirstName(teacher.getFirstName());
            existingTeacher.setLastName(teacher.getLastName());
            existingTeacher.setEmail(teacher.getEmail());
            teacherService.updateTeacher(existingTeacher);
            return "redirect:/teachers";
        } catch (DuplicateResourceException e) {
            model.addAttribute("error", e.getMessage());
            return "edit_teacher";
        }
    }

    @GetMapping("/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            teacherService.deleteTeacherById(id);
            return "redirect:/teachers";
        } catch (ResourceInUseException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/teachers";
        }
    }
}