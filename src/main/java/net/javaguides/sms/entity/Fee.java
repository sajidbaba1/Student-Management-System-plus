package net.javaguides.sms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fees")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false)
    private String feeType; // TUITION, LIBRARY, TRANSPORT, EXAM, LATE_FEE

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, PAID, OVERDUE

    @Column
    private Double paidAmount = 0.0;

    @Column
    private LocalDateTime paidAt;

    @Column
    private String paymentMethod; // CASH, CARD, BANK_TRANSFER, ONLINE

    @Column
    private String transactionId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Fee() {}

    public Fee(Student student, String feeType, Double amount, LocalDate dueDate) {
        this.student = student;
        this.feeType = feeType;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public Double getBalanceAmount() {
        return amount - paidAmount;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !"PAID".equals(status);
    }

    public void markAsPaid(Double amount, String paymentMethod, String transactionId) {
        this.paidAmount += amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paidAt = LocalDateTime.now();
        if (this.paidAmount >= this.amount) {
            this.status = "PAID";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
