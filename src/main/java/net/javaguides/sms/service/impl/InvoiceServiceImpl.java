package net.javaguides.sms.service.impl;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
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
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            Paragraph title = new Paragraph("INVOICE")
                    .setFontSize(22)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(33, 150, 243));
            doc.add(title);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Paragraph meta = new Paragraph(
                    "Invoice ID: " + invoice.getId() + "\n" +
                    "Issue Date: " + invoice.getIssueDate().format(fmt) + "\n" +
                    "Due Date: " + invoice.getDueDate().format(fmt))
                    .setMarginTop(10);
            doc.add(meta);

            Paragraph student = new Paragraph("Billed To:\n" +
                    invoice.getStudent().getFirstName() + " " + invoice.getStudent().getLastName() +
                    " (" + invoice.getStudent().getEmail() + ")")
                    .setMarginTop(10);
            doc.add(student);

            Table table = new Table(new float[]{70, 30});
            table.setWidthPercent(100);
            table.addHeaderCell("Description");
            table.addHeaderCell("Amount ($)");
            table.addCell(invoice.getDescription());
            table.addCell(String.format("%.2f", invoice.getAmount()));
            table.setBorder(new SolidBorder(1));
            doc.add(table);

            Paragraph status = new Paragraph("Status: " + invoice.getStatus())
                    .setMarginTop(10);
            doc.add(status);

            Paragraph footer = new Paragraph("Thank you for your payment.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            doc.add(footer);

            doc.close();
            return baos;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
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
