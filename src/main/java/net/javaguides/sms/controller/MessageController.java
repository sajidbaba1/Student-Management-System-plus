package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Message;
import net.javaguides.sms.service.MessageService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String inbox(Authentication auth, Model model) {
        String username = auth.getName();
        model.addAttribute("messages", messageService.getInboxMessages(username));
        model.addAttribute("unreadCount", messageService.getUnreadCount(username));
        model.addAttribute("currentTab", "inbox");
        return "messages";
    }

    @GetMapping("/sent")
    public String sentMessages(Authentication auth, Model model) {
        String username = auth.getName();
        model.addAttribute("messages", messageService.getSentMessages(username));
        model.addAttribute("currentTab", "sent");
        return "messages";
    }

    @GetMapping("/announcements")
    public String announcements(Model model) {
        model.addAttribute("messages", messageService.getAnnouncements());
        model.addAttribute("currentTab", "announcements");
        return "messages";
    }

    @GetMapping("/compose")
    public String composeForm(Model model) {
        model.addAttribute("message", new Message());
        return "compose_message";
    }

    @PostMapping("/send")
    public String sendMessage(@ModelAttribute Message message,
                             Authentication auth,
                             RedirectAttributes ra) {
        message.setSenderUsername(auth.getName());
        messageService.sendMessage(message);
        ra.addFlashAttribute("success", "Message sent successfully");
        return "redirect:/messages";
    }

    @PostMapping("/broadcast")
    public String broadcastAnnouncement(@RequestParam String subject,
                                       @RequestParam String content,
                                       Authentication auth,
                                       RedirectAttributes ra) {
        messageService.broadcastAnnouncement(subject, content, auth.getName());
        ra.addFlashAttribute("success", "Announcement broadcasted successfully");
        return "redirect:/messages/announcements";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id, RedirectAttributes ra) {
        messageService.markAsRead(id);
        return "redirect:/messages";
    }

    @PostMapping("/{id}/delete")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes ra) {
        messageService.deleteMessage(id);
        ra.addFlashAttribute("success", "Message deleted successfully");
        return "redirect:/messages";
    }
}
