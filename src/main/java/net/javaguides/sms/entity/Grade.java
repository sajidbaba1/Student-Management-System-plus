package net.javaguides.sms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(nullable = false)
    private String assessmentType; // QUIZ, ASSIGNMENT, MIDTERM, FINAL, PROJECT

    @Column(nullable = false)
    private String assessmentName;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private Double maxScore;

    @Column
    private String comments;

    @Column(nullable = false)
    private LocalDateTime gradedAt = LocalDateTime.now();

    public Grade() {}

    public Grade(Student student, Course course, Teacher teacher, String assessmentType, 
                 String assessmentName, Double score, Double maxScore) {
        this.student = student;
        this.course = course;
        this.teacher = teacher;
        this.assessmentType = assessmentType;
        this.assessmentName = assessmentName;
        this.score = score;
        this.maxScore = maxScore;
    }

    // Calculate percentage
    public Double getPercentage() {
        return maxScore > 0 ? (score / maxScore) * 100 : 0.0;
    }

    // Calculate GPA point (4.0 scale)
    public Double getGpaPoint() {
        double percentage = getPercentage();
        if (percentage >= 90) return 4.0;
        if (percentage >= 80) return 3.0;
        if (percentage >= 70) return 2.0;
        if (percentage >= 60) return 1.0;
        return 0.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }
    public String getAssessmentName() { return assessmentName; }
    public void setAssessmentName(String assessmentName) { this.assessmentName = assessmentName; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }
}
