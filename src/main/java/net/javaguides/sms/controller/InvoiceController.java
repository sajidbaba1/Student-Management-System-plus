package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Invoice;
import net.javaguides.sms.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','ACCOUNTANT','PRINCIPAL','VICE_PRINCIPAL','PARENT')")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<Invoice> invoices = invoiceService.list(PageRequest.of(page, size));
        model.addAttribute("invoices", invoices.getContent());
        model.addAttribute("page", invoices);
        return "invoices";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','ACCOUNTANT','PRINCIPAL','VICE_PRINCIPAL')")
    public String create(@RequestParam Long studentId,
                         @RequestParam String description,
                         @RequestParam Double amount,
                         @RequestParam String dueDate) {
        invoiceService.create(studentId, description, amount, LocalDate.parse(dueDate));
        return "redirect:/invoices";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','ACCOUNTANT','PRINCIPAL','VICE_PRINCIPAL','PARENT')")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.getById(id));
        return "invoice_show";
    }

    @PostMapping("/{id}/paid")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','ACCOUNTANT','PRINCIPAL','VICE_PRINCIPAL')")
    public String markPaid(@PathVariable Long id) {
        invoiceService.markPaid(id);
        return "redirect:/invoices/" + id;
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','ACCOUNTANT','PRINCIPAL','VICE_PRINCIPAL','PARENT')")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        Invoice invoice = invoiceService.getById(id);
        ByteArrayOutputStream baos = invoiceService.generatePdf(invoice);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
    }

    @PostMapping("/{id}/email")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','ACCOUNTANT','PRINCIPAL','VICE_PRINCIPAL')")
    public String email(@PathVariable Long id, @RequestParam String to) {
        invoiceService.emailInvoice(id, to);
        return "redirect:/invoices/" + id;
    }
}
