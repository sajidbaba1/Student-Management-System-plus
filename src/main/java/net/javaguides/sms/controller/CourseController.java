package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Course;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.exception.ResourceInUseException;
import net.javaguides.sms.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "sort", defaultValue = "courseName") String sort,
                              @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        Sort sortObj = ("desc".equalsIgnoreCase(direction)) ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Course> coursePage = courseService.searchCourses(keyword, pageable);
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", coursePage.getNumber());
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalItems", coursePage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        return "courses";
    }

    @GetMapping("/export")
    public void exportCoursesCsv(HttpServletResponse response,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "sort", defaultValue = "courseName") String sort,
                                 @RequestParam(value = "direction", defaultValue = "asc") String direction) throws IOException {
        Sort sortObj = ("desc".equalsIgnoreCase(direction)) ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Course> coursePage = courseService.searchCourses(keyword, pageable);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=courses.csv");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("Course Name,Description");
            for (Course c : coursePage.getContent()) {
                String name = c.getCourseName() != null ? c.getCourseName().replaceAll(",", " ") : "";
                String desc = c.getDescription() != null ? c.getDescription().replaceAll(",", " ") : "";
                writer.printf("%s,%s%n", name, desc);
            }
        }
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