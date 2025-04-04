package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Timetable;
import net.javaguides.sms.exception.DuplicateResourceException;
import net.javaguides.sms.repository.TimetableRepository;
import net.javaguides.sms.service.TimetableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimetableServiceImpl implements TimetableService {

    private final TimetableRepository timetableRepository;

    public TimetableServiceImpl(TimetableRepository timetableRepository) {
        this.timetableRepository = timetableRepository;
    }

    @Override
    public Page<Timetable> getAllTimetables(Pageable pageable) {
        return timetableRepository.findAll(pageable);
    }

    @Override
    public Timetable saveTimetable(Timetable timetable) {
        checkForOverlaps(timetable, null);
        return timetableRepository.save(timetable);
    }

    @Override
    public Timetable getTimetableById(Long id) {
        return timetableRepository.findById(id).orElseThrow(() -> new RuntimeException("Timetable not found"));
    }

    @Override
    public Timetable updateTimetable(Timetable timetable) {
        checkForOverlaps(timetable, timetable.getId());
        return timetableRepository.save(timetable);
    }

    @Override
    public void deleteTimetableById(Long id) {
        timetableRepository.deleteById(id);
    }

    @Override
    public List<Timetable> findByTeacherId(Long teacherId) {
        return timetableRepository.findByTeacherId(teacherId);
    }

    @Override
    public List<Timetable> findByCourseId(Long courseId) {
        return timetableRepository.findByCourseId(courseId);
    }

    private void checkForOverlaps(Timetable timetable, Long timetableId) {
        Long id = timetableId != null ? timetableId : -1L; // Use -1 for new timetables
        List<Timetable> overlappingTimetables = timetableRepository.findOverlappingTimetables(
                timetable.getTeacher().getId(),
                timetable.getDayOfWeek(),
                timetable.getStartTime(),
                timetable.getEndTime(),
                id
        );
        if (!overlappingTimetables.isEmpty()) {
            throw new DuplicateResourceException("The teacher is already scheduled for another class on " +
                    timetable.getDayOfWeek() + " from " + timetable.getStartTime() + " to " + timetable.getEndTime() + ".");
        }
    }
}