package net.javaguides.sms.service;

import net.javaguides.sms.entity.Timetable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TimetableService {
    Page<Timetable> getAllTimetables(Pageable pageable);
    Timetable saveTimetable(Timetable timetable);
    Timetable getTimetableById(Long id);
    Timetable updateTimetable(Timetable timetable);
    void deleteTimetableById(Long id);
    List<Timetable> findByTeacherId(Long teacherId);
    List<Timetable> findByCourseId(Long courseId);
}