package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Attendance;
import net.javaguides.sms.entity.Timetable;
import net.javaguides.sms.service.AttendanceService;
import net.javaguides.sms.service.TimetableService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final TimetableService timetableService;

    public AttendanceController(AttendanceService attendanceService, TimetableService timetableService) {
        this.attendanceService = attendanceService;
        this.timetableService = timetableService;
    }

    @GetMapping("/mark/{timetableId}")
    public String markPage(@PathVariable Long timetableId,
                           @RequestParam(value = "date", required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           Model model) {
        Timetable t = timetableService.getTimetableById(timetableId);
        if (date == null) date = LocalDate.now();
        List<Attendance> records = attendanceService.listByTimetableAndDate(timetableId, date);
        model.addAttribute("timetable", t);
        model.addAttribute("date", date);
        model.addAttribute("records", records);
        return "mark_attendance";
    }

    @PostMapping("/mark")
    public String mark(@RequestParam Long studentId,
                       @RequestParam Long timetableId,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam String status,
                       RedirectAttributes ra) {
        attendanceService.mark(studentId, timetableId, date, status);
        ra.addFlashAttribute("message", "Attendance updated");
        return "redirect:/attendance/mark/" + timetableId + "?date=" + date;
    }

    @GetMapping("/report/{studentId}")
    public String report(@PathVariable Long studentId, Model model) {
        List<Attendance> list = attendanceService.listByStudent(studentId);
        long total = list.size();
        long present = list.stream().filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus())).count();
        double percent = total == 0 ? 0.0 : (present * 100.0 / total);
        model.addAttribute("records", list);
        model.addAttribute("total", total);
        model.addAttribute("present", present);
        model.addAttribute("percent", percent);
        model.addAttribute("studentId", studentId);
        return "attendance_report";
    }
}
