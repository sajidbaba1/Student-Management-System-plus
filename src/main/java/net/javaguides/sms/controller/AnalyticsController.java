package net.javaguides.sms.controller;

import net.javaguides.sms.service.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;
    private final GradeService gradeService;
    private final FeeService feeService;
    private final AttendanceService attendanceService;
    private final EnrollmentService enrollmentService;

    public AnalyticsController(StudentService studentService, TeacherService teacherService, 
                             CourseService courseService, GradeService gradeService,
                             FeeService feeService, AttendanceService attendanceService,
                             EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.courseService = courseService;
        this.gradeService = gradeService;
        this.feeService = feeService;
        this.attendanceService = attendanceService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/students-by-course")
    @ResponseBody
    public Map<String, Object> getStudentsByCourse() {
        Map<String, Object> data = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        
        courseService.getAllCourses().forEach(course -> {
            labels.add(course.getCourseName());
            int count = (int) enrollmentService.findByCourse(course.getId()).stream()
                    .filter(e -> "ENROLLED".equalsIgnoreCase(e.getStatus()))
                    .count();
            values.add(count);
        });
        
        data.put("labels", labels);
        data.put("datasets", Arrays.asList(Map.of(
            "label", "Students per Course",
            "data", values,
            "backgroundColor", Arrays.asList(
                "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"
            )
        )));
        
        return data;
    }

    @GetMapping("/grade-distribution")
    @ResponseBody
    public Map<String, Object> getGradeDistribution() {
        Map<String, Object> data = new HashMap<>();
        Map<String, Integer> gradeCount = new HashMap<>();
        gradeCount.put("A (90-100)", 0);
        gradeCount.put("B (80-89)", 0);
        gradeCount.put("C (70-79)", 0);
        gradeCount.put("D (60-69)", 0);
        gradeCount.put("F (0-59)", 0);
        
        gradeService.getAllGrades(Pageable.unpaged()).getContent().forEach(grade -> {
            double percentage = grade.getPercentage();
            if (percentage >= 90) gradeCount.put("A (90-100)", gradeCount.get("A (90-100)") + 1);
            else if (percentage >= 80) gradeCount.put("B (80-89)", gradeCount.get("B (80-89)") + 1);
            else if (percentage >= 70) gradeCount.put("C (70-79)", gradeCount.get("C (70-79)") + 1);
            else if (percentage >= 60) gradeCount.put("D (60-69)", gradeCount.get("D (60-69)") + 1);
            else gradeCount.put("F (0-59)", gradeCount.get("F (0-59)") + 1);
        });
        
        data.put("labels", new ArrayList<>(gradeCount.keySet()));
        data.put("datasets", Arrays.asList(Map.of(
            "label", "Grade Distribution",
            "data", new ArrayList<>(gradeCount.values()),
            "backgroundColor", Arrays.asList("#28a745", "#17a2b8", "#ffc107", "#fd7e14", "#dc3545")
        )));
        
        return data;
    }

    @GetMapping("/attendance-trends")
    @ResponseBody
    public Map<String, Object> getAttendanceTrends() {
        Map<String, Object> data = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> attendanceRates = new ArrayList<>();
        
        // Get last 7 days attendance
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            labels.add(date.format(DateTimeFormatter.ofPattern("MM/dd")));
            
            long totalStudents = studentService.getAllStudents().size();
            long presentStudents = studentService.getAllStudents().stream()
                .mapToLong(s -> attendanceService.listByStudent(s.getId()).stream()
                        .filter(a -> date.equals(a.getDate()) && "PRESENT".equalsIgnoreCase(a.getStatus()))
                        .count())
                .sum();
            
            double rate = totalStudents > 0 ? (presentStudents * 100.0 / totalStudents) : 0;
            attendanceRates.add(Math.round(rate * 100.0) / 100.0);
        }
        
        data.put("labels", labels);
        data.put("datasets", Arrays.asList(Map.of(
            "label", "Attendance Rate (%)",
            "data", attendanceRates,
            "borderColor", "#007bff",
            "backgroundColor", "rgba(0, 123, 255, 0.1)",
            "fill", true
        )));
        
        return data;
    }

    @GetMapping("/fee-collection")
    @ResponseBody
    public Map<String, Object> getFeeCollection() {
        Map<String, Object> data = new HashMap<>();
        List<String> labels = Arrays.asList("Paid", "Pending", "Overdue");
        List<Double> values = new ArrayList<>();
        
        double totalPaid = studentService.getAllStudents().stream()
                .mapToDouble(s -> Optional.ofNullable(feeService.getTotalPaid(s.getId())).orElse(0.0))
                .sum();
        double totalPending = studentService.getAllStudents().stream()
                .mapToDouble(s -> Optional.ofNullable(feeService.getTotalOutstanding(s.getId())).orElse(0.0))
                .sum();
        double totalOverdue = feeService.getOverdueFees().stream()
            .mapToDouble(fee -> Optional.ofNullable(fee.getAmount()).orElse(0.0)).sum();
        
        values.add(totalPaid);
        values.add(totalPending - totalOverdue);
        values.add(totalOverdue);
        
        data.put("labels", labels);
        data.put("datasets", Arrays.asList(Map.of(
            "label", "Fee Collection ($)",
            "data", values,
            "backgroundColor", Arrays.asList("#28a745", "#ffc107", "#dc3545")
        )));
        
        return data;
    }

    @GetMapping("/export/students")
    public ResponseEntity<byte[]> exportStudents() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        
        // CSV Header
        writer.println("ID,First Name,Last Name,Email,Active");
        
        // CSV Data
        studentService.getAllStudents().forEach(student -> {
            writer.printf("%d,%s,%s,%s,%s%n",
                student.getId(),
                escapeCSV(student.getFirstName()),
                escapeCSV(student.getLastName()),
                escapeCSV(student.getEmail()),
                String.valueOf(student.isActive())
            );
        });
        
        writer.flush();
        writer.close();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "students_" + LocalDate.now() + ".csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(baos.toByteArray());
    }

    @GetMapping("/export/grades")
    public ResponseEntity<byte[]> exportGrades() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        
        // CSV Header
        writer.println("Student Name,Course,Teacher,Assessment Type,Score,Max Score,Percentage,GPA,Comments");
        
        // CSV Data
        gradeService.getAllGrades(Pageable.unpaged()).getContent().forEach(grade -> {
            writer.printf("%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%s%n",
                escapeCSV(grade.getStudent().getFirstName() + " " + grade.getStudent().getLastName()),
                escapeCSV(grade.getCourse().getCourseName()),
                escapeCSV(grade.getTeacher().getFirstName() + " " + grade.getTeacher().getLastName()),
                escapeCSV(grade.getAssessmentType()),
                grade.getScore(),
                grade.getMaxScore(),
                grade.getPercentage(),
                grade.getGpaPoint(),
                escapeCSV(grade.getComments())
            );
        });
        
        writer.flush();
        writer.close();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "grades_" + LocalDate.now() + ".csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(baos.toByteArray());
    }

    @GetMapping("/export/fees")
    public ResponseEntity<byte[]> exportFees() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        
        // CSV Header
        writer.println("Student Name,Fee Type,Amount,Due Date,Status,Payment Date,Payment Method");
        
        // CSV Data
        feeService.getAllFees(Pageable.unpaged()).getContent().forEach(fee -> {
            writer.printf("%s,%s,%.2f,%s,%s,%s,%s%n",
                escapeCSV(fee.getStudent().getFirstName() + " " + fee.getStudent().getLastName()),
                escapeCSV(fee.getFeeType()),
                fee.getAmount(),
                fee.getDueDate(),
                escapeCSV(fee.getStatus()),
                fee.getPaidAt() != null ? fee.getPaidAt().toString() : "",
                escapeCSV(fee.getPaymentMethod())
            );
        });
        
        writer.flush();
        writer.close();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "fees_" + LocalDate.now() + ".csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(baos.toByteArray());
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
