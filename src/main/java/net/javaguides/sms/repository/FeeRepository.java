package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByStudent_Id(Long studentId);
    List<Fee> findByStatus(String status);
    List<Fee> findByFeeType(String feeType);
    List<Fee> findByDueDateBefore(LocalDate date);
    List<Fee> findByStudent_IdAndStatus(Long studentId, String status);
    
    @Query("SELECT SUM(f.amount) FROM Fee f WHERE f.student.id = :studentId AND f.status = 'PAID'")
    Double getTotalPaidByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT SUM(f.amount - f.paidAmount) FROM Fee f WHERE f.student.id = :studentId AND f.status != 'PAID'")
    Double getTotalOutstandingByStudent(@Param("studentId") Long studentId);
}
