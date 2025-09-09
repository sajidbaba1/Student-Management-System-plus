package net.javaguides.sms.service;

import net.javaguides.sms.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface InvoiceService {
    Invoice create(Long studentId, String description, Double amount, java.time.LocalDate dueDate);
    List<Invoice> listAll();
    Page<Invoice> list(Pageable pageable);
    Invoice getById(Long id);
    Invoice markPaid(Long id);
    ByteArrayOutputStream generatePdf(Invoice invoice);
    void emailInvoice(Long id, String toEmail);
    List<Invoice> listByStudent(Long studentId);
}
