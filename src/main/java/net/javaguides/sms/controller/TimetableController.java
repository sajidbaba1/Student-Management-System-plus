package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Timetable;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.service.CourseService;
import net.javaguides.sms.service.TeacherService;
import net.javaguides.sms.service.TimetableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/timetables")
public class TimetableController {

    private final TimetableService timetableService;
    private final TeacherService teacherService;
    private final CourseService courseService;

    public TimetableController(TimetableService timetableService, TeacherService teacherService, CourseService courseService) {
        this.timetableService = timetableService;
        this.teacherService = teacherService;
        this.courseService = courseService;
    }

    @GetMapping
    public String listTimetables(Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Timetable> timetablePage = timetableService.getAllTimetables(pageable);
        model.addAttribute("timetables", timetablePage.getContent());
        model.addAttribute("currentPage", timetablePage.getNumber());
        model.addAttribute("totalPages", timetablePage.getTotalPages());
        model.addAttribute("totalItems", timetablePage.getTotalElements());
        return "timetables";
    }

    @GetMapping("/new")
    public String createTimetableForm(Model model) {
        model.addAttribute("timetable", new Timetable());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("courses", courseService.getAllCourses());
        return "create_timetable";
    }

    @PostMapping
    public String saveTimetable(@ModelAttribute("timetable") Timetable timetable, RedirectAttributes redirectAttributes) {
        try {
            timetableService.saveTimetable(timetable);
            return "redirect:/timetables";
        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/timetables/new";
        }
    }

    @GetMapping("/edit/{id}")
    public String editTimetableForm(@PathVariable Long id, Model model) {
        model.addAttribute("timetable", timetableService.getTimetableById(id));
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("courses", courseService.getAllCourses());
        return "edit_timetable";
    }

    @PostMapping("/{id}")
    public String updateTimetable(@PathVariable Long id, @ModelAttribute("timetable") Timetable timetable, RedirectAttributes redirectAttributes) {
        try {
            Timetable existingTimetable = timetableService.getTimetableById(id);
            existingTimetable.setTeacher(timetable.getTeacher());
            existingTimetable.setCourse(timetable.getCourse());
            existingTimetable.setDayOfWeek(timetable.getDayOfWeek());
            existingTimetable.setStartTime(timetable.getStartTime());
            existingTimetable.setEndTime(timetable.getEndTime());
            timetableService.updateTimetable(existingTimetable);
            return "redirect:/timetables";
        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/timetables/edit/" + id;
        }
    }

    @GetMapping("/{id}")
    public String deleteTimetable(@PathVariable Long id) {
        timetableService.deleteTimetableById(id);
        return "redirect:/timetables";
    }
}