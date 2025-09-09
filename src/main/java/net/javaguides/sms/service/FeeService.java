package net.javaguides.sms.service;

import net.javaguides.sms.entity.Fee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeeService {
    Fee saveFee(Fee fee);
    List<Fee> getFeesByStudent(Long studentId);
    List<Fee> getPendingFees();
    List<Fee> getOverdueFees();
    Fee processPayment(Long feeId, Double amount, String paymentMethod, String transactionId);
    Double getTotalOutstanding(Long studentId);
    Double getTotalPaid(Long studentId);
    Page<Fee> getAllFees(Pageable pageable);
    void deleteFee(Long feeId);
    Fee getFeeById(Long feeId);
}
