package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByStudent_IdAndTimetable_IdAndDate(Long studentId, Long timetableId, LocalDate date);
    List<Attendance> findByTimetable_IdAndDate(Long timetableId, LocalDate date);
    List<Attendance> findByStudent_Id(Long studentId);
}
