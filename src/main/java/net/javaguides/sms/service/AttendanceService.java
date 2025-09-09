package net.javaguides.sms.service;

import net.javaguides.sms.entity.Attendance;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Attendance mark(Long studentId, Long timetableId, LocalDate date, String status);
    List<Attendance> listByTimetableAndDate(Long timetableId, LocalDate date);
    List<Attendance> listByStudent(Long studentId);
}
