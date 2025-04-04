package net.javaguides.sms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames = "course_name"))
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "description")
    private String description;

    // Constructors
    public Course() {}

    public Course(String courseName, String description) {
        this.courseName = courseName;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}