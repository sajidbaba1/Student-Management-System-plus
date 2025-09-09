package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Fee;
import net.javaguides.sms.service.FeeService;
import net.javaguides.sms.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fees")
public class FeeController {

    private final FeeService feeService;
    private final StudentService studentService;

    public FeeController(FeeService feeService, StudentService studentService) {
        this.feeService = feeService;
        this.studentService = studentService;
    }

    @GetMapping
    public String listFees(@RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size,
                          Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Fee> fees = feeService.getAllFees(pageable);
        
        model.addAttribute("fees", fees.getContent());
        model.addAttribute("currentPage", fees.getNumber());
        model.addAttribute("totalPages", fees.getTotalPages());
        model.addAttribute("totalItems", fees.getTotalElements());
        return "fees";
    }

    @GetMapping("/new")
    public String newFeeForm(Model model) {
        model.addAttribute("fee", new Fee());
        model.addAttribute("students", studentService.getAllStudents());
        return "create_fee";
    }

    @PostMapping
    public String saveFee(@ModelAttribute Fee fee, RedirectAttributes ra) {
        feeService.saveFee(fee);
        ra.addFlashAttribute("message", "Fee record created successfully");
        return "redirect:/fees";
    }

    @GetMapping("/student/{studentId}")
    public String studentFees(@PathVariable Long studentId, Model model) {
        model.addAttribute("fees", feeService.getFeesByStudent(studentId));
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("totalOutstanding", feeService.getTotalOutstanding(studentId));
        model.addAttribute("totalPaid", feeService.getTotalPaid(studentId));
        return "student_fees";
    }

    @GetMapping("/overdue")
    public String overdueFees(Model model) {
        model.addAttribute("fees", feeService.getOverdueFees());
        return "overdue_fees";
    }

    @PostMapping("/{id}/pay")
    public String processPayment(@PathVariable Long id,
                                @RequestParam Double amount,
                                @RequestParam String paymentMethod,
                                @RequestParam(required = false) String transactionId,
                                RedirectAttributes ra) {
        feeService.processPayment(id, amount, paymentMethod, transactionId);
        ra.addFlashAttribute("message", "Payment processed successfully");
        return "redirect:/fees";
    }

    @PostMapping("/{id}/delete")
    public String deleteFee(@PathVariable Long id, RedirectAttributes ra) {
        feeService.deleteFee(id);
        ra.addFlashAttribute("message", "Fee record deleted successfully");
        return "redirect:/fees";
    }
}
