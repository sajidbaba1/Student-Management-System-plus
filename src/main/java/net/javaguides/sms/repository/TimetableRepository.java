package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    @Query("SELECT t FROM Timetable t WHERE t.teacher.id = :teacherId")
    List<Timetable> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT t FROM Timetable t WHERE t.course.id = :courseId")
    List<Timetable> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT t FROM Timetable t WHERE t.teacher.id = :teacherId AND t.dayOfWeek = :dayOfWeek " +
           "AND ((t.startTime <= :endTime AND t.endTime >= :startTime)) AND t.id != :timetableId")
    List<Timetable> findOverlappingTimetables(
            @Param("teacherId") Long teacherId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("timetableId") Long timetableId
    );
}