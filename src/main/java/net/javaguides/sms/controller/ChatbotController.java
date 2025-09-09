package net.javaguides.sms.controller;

import net.javaguides.sms.entity.ChatMessage;
import net.javaguides.sms.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @GetMapping
    public String chatPage(Model model) {
        model.addAttribute("totalMessagesToday", chatbotService.getTotalMessagesToday());
        model.addAttribute("topIntents", chatbotService.getTopIntents());
        model.addAttribute("geminiActive", geminiApiKey != null && !geminiApiKey.isBlank());
        return "chatbot";
    }

    @PostMapping("/message")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam String message,
            @RequestParam(required = false) String sessionId,
            Authentication auth) {
        // Handle unauthenticated access gracefully
        String username = (auth != null) ? auth.getName() : "guest";
        
        // Generate session ID if not provided
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = chatbotService.generateSessionId();
        }
        
        // Process the message
        String response = chatbotService.processMessage(sessionId, username, message);
        
        Map<String, Object> result = new HashMap<>();
        result.put("response", response);
        result.put("sessionId", sessionId);
        result.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestParam String sessionId) {
        
        List<ChatMessage> history = chatbotService.getChatHistory(sessionId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user-history")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getUserHistory(Authentication auth) {
        String username = auth.getName();
        List<ChatMessage> history = chatbotService.getUserChatHistory(username);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/analytics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChatAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalMessagesToday", chatbotService.getTotalMessagesToday());
        analytics.put("recentMessages", chatbotService.getRecentMessages(24).size());
        analytics.put("unresolvedMessages", chatbotService.getUnresolvedMessages().size());
        analytics.put("topIntents", chatbotService.getTopIntents());
        
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> status() {
        return Map.of("geminiActive", geminiApiKey != null && !geminiApiKey.isBlank());
    }

    @GetMapping("/widget")
    public String chatWidget() {
        return "fragments/chat_widget";
    }
}
