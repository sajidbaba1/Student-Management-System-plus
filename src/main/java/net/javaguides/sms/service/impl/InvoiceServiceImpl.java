package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Invoice;
import net.javaguides.sms.entity.Student;
import net.javaguides.sms.repository.InvoiceRepository;
import net.javaguides.sms.repository.StudentRepository;
import net.javaguides.sms.service.InvoiceService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final StudentRepository studentRepository;
    private final JavaMailSender mailSender;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              StudentRepository studentRepository,
                              JavaMailSender mailSender) {
        this.invoiceRepository = invoiceRepository;
        this.studentRepository = studentRepository;
        this.mailSender = mailSender;
    }

    @Override
    public Invoice create(Long studentId, String description, Double amount, LocalDate dueDate) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Invoice invoice = new Invoice(student, dueDate, description, amount);
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> listAll() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> list(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getById(Long id) {
        return invoiceRepository.findById(id).orElseThrow();
    }

    @Override
    public Invoice markPaid(Long id) {
        Invoice inv = getById(id);
        inv.setStatus("PAID");
        return invoiceRepository.save(inv);
    }

    @Override
    public ByteArrayOutputStream generatePdf(Invoice invoice) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String html = "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Invoice " + invoice.getId() + "</title>" +
                "<style>body{font-family:Arial,sans-serif;color:#333;padding:20px;}" +
                ".hdr{color:#2196f3;text-align:center;font-size:24px;font-weight:bold;}" +
                ".box{border:1px solid #ddd;border-radius:8px;padding:16px;margin-top:12px;}" +
                ".row{display:flex;justify-content:space-between;margin:6px 0;}" +
                "table{width:100%;border-collapse:collapse;margin-top:10px;}th,td{border:1px solid #ddd;padding:8px;}th{background:#f7f7f7;text-align:left;}" +
                ".ft{text-align:center;color:#666;margin-top:16px;}" +
                "</style></head><body>" +
                "<div class='hdr'>INVOICE</div>" +
                "<div class='box'>" +
                "<div class='row'><div><b>Invoice ID:</b> " + invoice.getId() + "</div><div><b>Status:</b> " + invoice.getStatus() + "</div></div>" +
                "<div class='row'><div><b>Issue Date:</b> " + invoice.getIssueDate().format(fmt) + "</div><div><b>Due Date:</b> " + invoice.getDueDate().format(fmt) + "</div></div>" +
                "</div>" +
                "<div class='box'>" +
                "<div><b>Billed To</b></div>" +
                "<div>" + invoice.getStudent().getFirstName() + " " + invoice.getStudent().getLastName() + " (" + invoice.getStudent().getEmail() + ")</div>" +
                "</div>" +
                "<div class='box'>" +
                "<table><thead><tr><th>Description</th><th>Amount ($)</th></tr></thead>" +
                "<tbody><tr><td>" + invoice.getDescription() + "</td><td>" + String.format("%.2f", invoice.getAmount()) + "</td></tr></tbody></table>" +
                "</div>" +
                "<div class='ft'>Thank you for your payment.</div>" +
                "</body></html>";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(html.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ignored) {}
        return baos;
    }

    @Override
    public void emailInvoice(Long id, String toEmail) {
        Invoice invoice = getById(id);
        // Simple text email with link/summary (attach PDF can be added later if needed)
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Invoice #" + invoice.getId());
        msg.setText("Dear " + invoice.getStudent().getFirstName() + ",\n\n" +
                "Here is your invoice summary:\n" +
                "Description: " + invoice.getDescription() + "\n" +
                "Amount: $" + String.format("%.2f", invoice.getAmount()) + "\n" +
                "Due: " + invoice.getDueDate() + "\n" +
                "Status: " + invoice.getStatus() + "\n\n" +
                "Regards,\nStudent Management System");
        mailSender.send(msg);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> listByStudent(Long studentId) {
        return invoiceRepository.findByStudent_Id(studentId);
    }
}
