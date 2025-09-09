package net.javaguides.sms.repository;

import net.javaguides.sms.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignment_Id(Long assignmentId);
    List<AssignmentSubmission> findByStudent_Id(Long studentId);
    Optional<AssignmentSubmission> findByAssignment_IdAndStudent_Id(Long assignmentId, Long studentId);
    List<AssignmentSubmission> findByScoreIsNull(); // Ungraded submissions
    List<AssignmentSubmission> findByScoreIsNotNull(); // Graded submissions
}
