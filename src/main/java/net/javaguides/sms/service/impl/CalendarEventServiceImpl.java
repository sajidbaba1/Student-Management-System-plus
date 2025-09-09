package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.CalendarEvent;
import net.javaguides.sms.repository.CalendarEventRepository;
import net.javaguides.sms.service.CalendarEventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class CalendarEventServiceImpl implements CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;

    public CalendarEventServiceImpl(CalendarEventRepository calendarEventRepository) {
        this.calendarEventRepository = calendarEventRepository;
    }

    @Override
    public CalendarEvent create(String title, String description, LocalDateTime startTime, LocalDateTime endTime,
                               String location, CalendarEvent.EventType eventType, CalendarEvent.RecurrenceType recurrence, String createdBy) {
        CalendarEvent event = new CalendarEvent(title, description, startTime, endTime, createdBy);
        event.setLocation(location);
        event.setEventType(eventType);
        event.setRecurrence(recurrence);
        return calendarEventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEvent> listAll() {
        return calendarEventRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CalendarEvent> list(Pageable pageable) {
        return calendarEventRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarEvent getById(Long id) {
        return calendarEventRepository.findById(id).orElseThrow();
    }

    @Override
    public CalendarEvent update(Long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime,
                               String location, CalendarEvent.EventType eventType, CalendarEvent.RecurrenceType recurrence) {
        CalendarEvent event = getById(id);
        event.setTitle(title);
        event.setDescription(description);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setLocation(location);
        event.setEventType(eventType);
        event.setRecurrence(recurrence);
        return calendarEventRepository.save(event);
    }

    @Override
    public void delete(Long id) {
        calendarEventRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEvent> getEventsBetween(LocalDateTime start, LocalDateTime end) {
        return calendarEventRepository.findEventsBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEvent> getUpcomingEvents() {
        return calendarEventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    @Override
    public String generateIcsExport(List<CalendarEvent> events) {
        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\r\n");
        ics.append("VERSION:2.0\r\n");
        ics.append("PRODID:-//Student Management System//Calendar//EN\r\n");
        ics.append("CALSCALE:GREGORIAN\r\n");

        DateTimeFormatter icsFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

        for (CalendarEvent event : events) {
            ics.append("BEGIN:VEVENT\r\n");
            ics.append("UID:").append(event.getId()).append("@sms.local\r\n");
            ics.append("DTSTART:").append(event.getStartTime().format(icsFormatter)).append("\r\n");
            ics.append("DTEND:").append(event.getEndTime().format(icsFormatter)).append("\r\n");
            ics.append("SUMMARY:").append(escapeIcsText(event.getTitle())).append("\r\n");
            if (event.getDescription() != null) {
                ics.append("DESCRIPTION:").append(escapeIcsText(event.getDescription())).append("\r\n");
            }
            if (event.getLocation() != null) {
                ics.append("LOCATION:").append(escapeIcsText(event.getLocation())).append("\r\n");
            }
            ics.append("CATEGORIES:").append(event.getEventType().name()).append("\r\n");
            ics.append("CREATED:").append(event.getCreatedAt().format(icsFormatter)).append("\r\n");
            ics.append("END:VEVENT\r\n");
        }

        ics.append("END:VCALENDAR\r\n");
        return ics.toString();
    }

    private String escapeIcsText(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace(",", "\\,")
                  .replace(";", "\\;")
                  .replace("\n", "\\n");
    }
}
