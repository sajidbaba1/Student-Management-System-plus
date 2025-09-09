package net.javaguides.sms.controller;

import net.javaguides.sms.entity.CalendarEvent;
import net.javaguides.sms.service.CalendarEventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarEventService calendarEventService;

    public CalendarController(CalendarEventService calendarEventService) {
        this.calendarEventService = calendarEventService;
    }

    @GetMapping
    public String calendar(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Model model) {
        Page<CalendarEvent> events = calendarEventService.list(PageRequest.of(page, size));
        model.addAttribute("events", events.getContent());
        model.addAttribute("page", events);
        model.addAttribute("upcomingEvents", calendarEventService.getUpcomingEvents());
        return "calendar";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','PRINCIPAL','VICE_PRINCIPAL','HOD')")
    public String create(@RequestParam String title,
                        @RequestParam String description,
                        @RequestParam String startTime,
                        @RequestParam String endTime,
                        @RequestParam(required = false) String location,
                        @RequestParam CalendarEvent.EventType eventType,
                        @RequestParam CalendarEvent.RecurrenceType recurrence,
                        Authentication auth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        calendarEventService.create(
            title, description,
            LocalDateTime.parse(startTime, formatter),
            LocalDateTime.parse(endTime, formatter),
            location, eventType, recurrence,
            auth.getName()
        );
        return "redirect:/calendar";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("event", calendarEventService.getById(id));
        return "calendar_event";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','PRINCIPAL','VICE_PRINCIPAL','HOD')")
    public String update(@PathVariable Long id,
                        @RequestParam String title,
                        @RequestParam String description,
                        @RequestParam String startTime,
                        @RequestParam String endTime,
                        @RequestParam(required = false) String location,
                        @RequestParam CalendarEvent.EventType eventType,
                        @RequestParam CalendarEvent.RecurrenceType recurrence) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        calendarEventService.update(
            id, title, description,
            LocalDateTime.parse(startTime, formatter),
            LocalDateTime.parse(endTime, formatter),
            location, eventType, recurrence
        );
        return "redirect:/calendar/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','PRINCIPAL','VICE_PRINCIPAL','HOD')")
    public String delete(@PathVariable Long id) {
        calendarEventService.delete(id);
        return "redirect:/calendar";
    }

    @GetMapping("/export.ics")
    public ResponseEntity<String> exportIcs(@RequestParam(required = false) String start,
                                           @RequestParam(required = false) String end) {
        List<CalendarEvent> events;
        if (start != null && end != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            events = calendarEventService.getEventsBetween(
                LocalDateTime.parse(start + "T00:00:00"),
                LocalDateTime.parse(end + "T23:59:59")
            );
        } else {
            events = calendarEventService.listAll();
        }
        
        String icsContent = calendarEventService.generateIcsExport(events);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "calendar.ics");
        
        return ResponseEntity.ok().headers(headers).body(icsContent);
    }
}
