package net.javaguides.sms.repository;

import net.javaguides.sms.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime >= :start AND e.endTime <= :end ORDER BY e.startTime")
    List<CalendarEvent> findEventsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime >= :now ORDER BY e.startTime LIMIT 5")
    List<CalendarEvent> findUpcomingEvents(@Param("now") LocalDateTime now);
    
    List<CalendarEvent> findByEventTypeOrderByStartTime(CalendarEvent.EventType eventType);
}
