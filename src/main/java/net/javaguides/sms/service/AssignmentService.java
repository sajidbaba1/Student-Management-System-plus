package net.javaguides.sms.service;

import net.javaguides.sms.entity.Assignment;
import net.javaguides.sms.entity.AssignmentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssignmentService {
    Assignment saveAssignment(Assignment assignment);
    List<Assignment> getAssignmentsByCourse(Long courseId);
    List<Assignment> getAssignmentsByTeacher(Long teacherId);
    List<Assignment> getActiveAssignments();
    List<Assignment> getOverdueAssignments();
    Assignment getAssignmentById(Long id);
    void deleteAssignment(Long id);
    Page<Assignment> getAllAssignments(Pageable pageable);
    
    // Submission methods
    AssignmentSubmission submitAssignment(AssignmentSubmission submission);
    List<AssignmentSubmission> getSubmissionsByAssignment(Long assignmentId);
    List<AssignmentSubmission> getSubmissionsByStudent(Long studentId);
    AssignmentSubmission gradeSubmission(Long submissionId, Double score, String feedback, Long teacherId);
    List<AssignmentSubmission> getUngradedSubmissions();
}
