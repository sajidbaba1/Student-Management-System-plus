package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Teacher;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.exception.ResourceInUseException;
import net.javaguides.sms.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                               @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Teacher> teacherPage = teacherService.searchTeachers(keyword, pageable);
        model.addAttribute("teachers", teacherPage.getContent());
        model.addAttribute("currentPage", teacherPage.getNumber());
        model.addAttribute("totalPages", teacherPage.getTotalPages());
        model.addAttribute("totalItems", teacherPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        return "teachers";
    }

    @GetMapping("/new")
    public String createTeacherForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "create_teacher";
    }

    @PostMapping
    public String saveTeacher(@ModelAttribute("teacher") Teacher teacher, RedirectAttributes redirectAttributes) {
        try {
            teacherService.saveTeacher(teacher);
            return "redirect:/teachers";
        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/teachers/new";
        }
    }

    @GetMapping("/edit/{id}")
    public String editTeacherForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "edit_teacher";
    }

    @PostMapping("/{id}")
    public String updateTeacher(@PathVariable Long id, @ModelAttribute("teacher") Teacher teacher, RedirectAttributes redirectAttributes) {
        try {
            Teacher existingTeacher = teacherService.getTeacherById(id);
            existingTeacher.setFirstName(teacher.getFirstName());
            existingTeacher.setLastName(teacher.getLastName());
            existingTeacher.setEmail(teacher.getEmail());
            teacherService.updateTeacher(existingTeacher);
            return "redirect:/teachers";
        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/teachers/edit/" + id;
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