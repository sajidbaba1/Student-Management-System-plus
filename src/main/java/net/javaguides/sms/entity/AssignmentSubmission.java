package net.javaguides.sms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions")
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String attachmentPath;

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column
    private Double score;

    @Column
    private String feedback;

    @Column
    private LocalDateTime gradedAt;

    @ManyToOne
    @JoinColumn(name = "graded_by")
    private Teacher gradedBy;

    public AssignmentSubmission() {}

    public AssignmentSubmission(Assignment assignment, Student student, String content) {
        this.assignment = assignment;
        this.student = student;
        this.content = content;
    }

    public boolean isLate() {
        return submittedAt.isAfter(assignment.getDueDate());
    }

    public boolean isGraded() {
        return score != null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }
    public Teacher getGradedBy() { return gradedBy; }
    public void setGradedBy(Teacher gradedBy) { this.gradedBy = gradedBy; }
}
