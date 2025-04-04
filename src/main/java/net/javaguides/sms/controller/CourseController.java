package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Course;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.exception.ResourceInUseException;
import net.javaguides.sms.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String listCourses(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size,
                              @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseService.searchCourses(keyword, pageable);
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", coursePage.getNumber());
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalItems", coursePage.getTotalElements());
        model.addAttribute("keyword", keyword);
        return "courses";
    }

    @GetMapping("/new")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "create_course";
    }

    @PostMapping
    public String saveCourse(@ModelAttribute("course") Course course, RedirectAttributes redirectAttributes) {
        try {
            courseService.saveCourse(course);
            return "redirect:/courses";
        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses/new";
        }
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "edit_course";
    }

    @PostMapping("/{id}")
    public String updateCourse(@PathVariable Long id, @ModelAttribute("course") Course course, RedirectAttributes redirectAttributes) {
        try {
            Course existingCourse = courseService.getCourseById(id);
            existingCourse.setCourseName(course.getCourseName());
            existingCourse.setDescription(course.getDescription());
            courseService.updateCourse(existingCourse);
            return "redirect:/courses";
        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses/edit/" + id;
        }
    }

    @GetMapping("/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourseById(id);
            return "redirect:/courses";
        } catch (ResourceInUseException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses";
        }
    }
}