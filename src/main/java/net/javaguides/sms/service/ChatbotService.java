package net.javaguides.sms.service;

import net.javaguides.sms.entity.ChatMessage;
import java.util.List;

public interface ChatbotService {
    
    String processMessage(String sessionId, String username, String message);
    
    List<ChatMessage> getChatHistory(String sessionId);
    
    List<ChatMessage> getUserChatHistory(String username);
    
    ChatMessage saveMessage(String sessionId, String username, String userMessage, String botResponse);
    
    String generateSessionId();
    
    List<ChatMessage> getRecentMessages(int hours);
    
    List<ChatMessage> getUnresolvedMessages();
    
    Long getTotalMessagesToday();
    
    List<Object[]> getTopIntents();
}
