package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Assignment;
import net.javaguides.sms.entity.AssignmentSubmission;
import net.javaguides.sms.entity.Teacher;
import net.javaguides.sms.repository.AssignmentRepository;
import net.javaguides.sms.repository.AssignmentSubmissionRepository;
import net.javaguides.sms.repository.TeacherRepository;
import net.javaguides.sms.service.AssignmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final TeacherRepository teacherRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository,
                                AssignmentSubmissionRepository submissionRepository,
                                TeacherRepository teacherRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Override
    public List<Assignment> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourse_Id(courseId);
    }

    @Override
    public List<Assignment> getAssignmentsByTeacher(Long teacherId) {
        return assignmentRepository.findByTeacher_Id(teacherId);
    }

    @Override
    public List<Assignment> getActiveAssignments() {
        return assignmentRepository.findByStatus("ACTIVE");
    }

    @Override
    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findByDueDateBeforeAndStatus(LocalDateTime.now(), "ACTIVE");
    }

    @Override
    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id).orElseThrow();
    }

    @Override
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    @Override
    public Page<Assignment> getAllAssignments(Pageable pageable) {
        return assignmentRepository.findAll(pageable);
    }

    @Override
    public AssignmentSubmission submitAssignment(AssignmentSubmission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public List<AssignmentSubmission> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignment_Id(assignmentId);
    }

    @Override
    public List<AssignmentSubmission> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudent_Id(studentId);
    }

    @Transactional
    @Override
    public AssignmentSubmission gradeSubmission(Long submissionId, Double score, String feedback, Long teacherId) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId).orElseThrow();
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        
        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setGradedBy(teacher);
        submission.setGradedAt(LocalDateTime.now());
        
        return submissionRepository.save(submission);
    }

    @Override
    public List<AssignmentSubmission> getUngradedSubmissions() {
        return submissionRepository.findByScoreIsNull();
    }
}
