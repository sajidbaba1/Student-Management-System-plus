package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Attendance;
import net.javaguides.sms.entity.Student;
import net.javaguides.sms.entity.Timetable;
import net.javaguides.sms.repository.AttendanceRepository;
import net.javaguides.sms.repository.StudentRepository;
import net.javaguides.sms.repository.TimetableRepository;
import net.javaguides.sms.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TimetableRepository timetableRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 StudentRepository studentRepository,
                                 TimetableRepository timetableRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.timetableRepository = timetableRepository;
    }

    @Transactional
    @Override
    public Attendance mark(Long studentId, Long timetableId, LocalDate date, String status) {
        Attendance att = attendanceRepository
                .findByStudent_IdAndTimetable_IdAndDate(studentId, timetableId, date)
                .orElseGet(() -> {
                    Student s = studentRepository.findById(studentId).orElseThrow();
                    Timetable t = timetableRepository.findById(timetableId).orElseThrow();
                    Attendance a = new Attendance();
                    a.setStudent(s);
                    a.setTimetable(t);
                    a.setDate(date);
                    return a;
                });
        att.setStatus(status);
        return attendanceRepository.save(att);
    }

    @Override
    public List<Attendance> listByTimetableAndDate(Long timetableId, LocalDate date) {
        return attendanceRepository.findByTimetable_IdAndDate(timetableId, date);
    }

    @Override
    public List<Attendance> listByStudent(Long studentId) {
        return attendanceRepository.findByStudent_Id(studentId);
    }
}
