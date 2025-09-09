package net.javaguides.sms.service;

import net.javaguides.sms.entity.CalendarEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarEventService {
    CalendarEvent create(String title, String description, LocalDateTime startTime, LocalDateTime endTime, 
                        String location, CalendarEvent.EventType eventType, CalendarEvent.RecurrenceType recurrence, String createdBy);
    List<CalendarEvent> listAll();
    Page<CalendarEvent> list(Pageable pageable);
    CalendarEvent getById(Long id);
    CalendarEvent update(Long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime, 
                        String location, CalendarEvent.EventType eventType, CalendarEvent.RecurrenceType recurrence);
    void delete(Long id);
    List<CalendarEvent> getEventsBetween(LocalDateTime start, LocalDateTime end);
    List<CalendarEvent> getUpcomingEvents();
    String generateIcsExport(List<CalendarEvent> events);
}
