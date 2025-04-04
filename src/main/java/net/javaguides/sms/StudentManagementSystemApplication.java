package net.javaguides.sms;

import net.javaguides.sms.entity.Student;
import net.javaguides.sms.entity.Teacher;
import net.javaguides.sms.entity.Course;
import net.javaguides.sms.entity.Timetable;
import net.javaguides.sms.repository.StudentRepository;
import net.javaguides.sms.repository.TeacherRepository;
import net.javaguides.sms.repository.CourseRepository;
import net.javaguides.sms.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentManagementSystemApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementSystemApplication.class, args);
    }

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TimetableRepository timetableRepository;

    @Override
    public void run(String... args) throws Exception {
        // Seed Students
        if (studentRepository.findByEmail("ramesh@gmail.com").isEmpty()) {
            Student student1 = new Student("Ramesh", "Fadatare", "ramesh@gmail.com");
            studentRepository.save(student1);
        }

        if (studentRepository.findByEmail("sanjay@gmail.com").isEmpty()) {
            Student student2 = new Student("Sanjay", "Jadhav", "sanjay@gmail.com");
            studentRepository.save(student2);
        }

        if (studentRepository.findByEmail("tony@gmail.com").isEmpty()) {
            Student student3 = new Student("Tony", "Stark", "tony@gmail.com");
            studentRepository.save(student3);
        }

        // Seed Teachers
        Teacher teacher1 = null;
        if (teacherRepository.findByEmail("alice.smith@gmail.com").isEmpty()) {
            teacher1 = new Teacher("Alice", "Smith", "alice.smith@gmail.com");
            teacherRepository.save(teacher1);
        } else {
            teacher1 = teacherRepository.findByEmail("alice.smith@gmail.com").get();
        }

        Teacher teacher2 = null;
        if (teacherRepository.findByEmail("bob.johnson@gmail.com").isEmpty()) {
            teacher2 = new Teacher("Bob", "Johnson", "bob.johnson@gmail.com");
            teacherRepository.save(teacher2);
        } else {
            teacher2 = teacherRepository.findByEmail("bob.johnson@gmail.com").get();
        }

        Teacher teacher3 = null;
        if (teacherRepository.findByEmail("carol.williams@gmail.com").isEmpty()) {
            teacher3 = new Teacher("Carol", "Williams", "carol.williams@gmail.com");
            teacherRepository.save(teacher3);
        } else {
            teacher3 = teacherRepository.findByEmail("carol.williams@gmail.com").get();
        }

        // Seed Courses
        Course course1 = null;
        if (courseRepository.findByCourseName("Mathematics").isEmpty()) {
            course1 = new Course("Mathematics", "Introduction to Algebra and Calculus");
            courseRepository.save(course1);
        } else {
            course1 = courseRepository.findByCourseName("Mathematics").get();
        }

        Course course2 = null;
        if (courseRepository.findByCourseName("Physics").isEmpty()) {
            course2 = new Course("Physics", "Fundamentals of Mechanics and Thermodynamics");
            courseRepository.save(course2);
        } else {
            course2 = courseRepository.findByCourseName("Physics").get();
        }

        Course course3 = null;
        if (courseRepository.findByCourseName("Computer Science").isEmpty()) {
            course3 = new Course("Computer Science", "Basics of Programming and Data Structures");
            courseRepository.save(course3);
        } else {
            course3 = courseRepository.findByCourseName("Computer Science").get();
        }

        // Seed Timetables
        Timetable timetable1 = new Timetable(teacher1, course1, "Monday", "09:00", "10:30");
        timetableRepository.save(timetable1);

        Timetable timetable2 = new Timetable(teacher2, course2, "Tuesday", "11:00", "12:30");
        timetableRepository.save(timetable2);

        Timetable timetable3 = new Timetable(teacher3, course3, "Wednesday", "14:00", "15:30");
        timetableRepository.save(timetable3);
    }
}