package net.javaguides.sms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "teachers", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is required")
    @Size(max = 255, message = "First name must be at most 255 characters")
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 255, message = "Last name must be at most 255 characters")
    private String lastName;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public Teacher() {}

    public Teacher(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}