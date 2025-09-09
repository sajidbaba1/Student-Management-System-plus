package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Fee;
import net.javaguides.sms.repository.FeeRepository;
import net.javaguides.sms.service.FeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;

    public FeeServiceImpl(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Override
    public Fee saveFee(Fee fee) {
        return feeRepository.save(fee);
    }

    @Override
    public List<Fee> getFeesByStudent(Long studentId) {
        return feeRepository.findByStudent_Id(studentId);
    }

    @Override
    public List<Fee> getPendingFees() {
        return feeRepository.findByStatus("PENDING");
    }

    @Override
    public List<Fee> getOverdueFees() {
        return feeRepository.findByDueDateBefore(LocalDate.now());
    }

    @Transactional
    @Override
    public Fee processPayment(Long feeId, Double amount, String paymentMethod, String transactionId) {
        Fee fee = feeRepository.findById(feeId).orElseThrow();
        fee.markAsPaid(amount, paymentMethod, transactionId);
        return feeRepository.save(fee);
    }

    @Override
    public Double getTotalOutstanding(Long studentId) {
        Double outstanding = feeRepository.getTotalOutstandingByStudent(studentId);
        return outstanding != null ? outstanding : 0.0;
    }

    @Override
    public Double getTotalPaid(Long studentId) {
        Double paid = feeRepository.getTotalPaidByStudent(studentId);
        return paid != null ? paid : 0.0;
    }

    @Override
    public Page<Fee> getAllFees(Pageable pageable) {
        return feeRepository.findAll(pageable);
    }

    @Override
    public void deleteFee(Long feeId) {
        feeRepository.deleteById(feeId);
    }

    @Override
    public Fee getFeeById(Long feeId) {
        return feeRepository.findById(feeId).orElseThrow();
    }
}
