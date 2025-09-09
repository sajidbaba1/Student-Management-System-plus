package net.javaguides.sms.service;

import net.javaguides.sms.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    Message sendMessage(Message message);
    List<Message> getInboxMessages(String username);
    List<Message> getSentMessages(String username);
    List<Message> getUnreadMessages(String username);
    List<Message> getAnnouncements();
    long getUnreadCount(String username);
    Message markAsRead(Long messageId);
    void deleteMessage(Long messageId);
    Page<Message> getAllMessages(Pageable pageable);
    Message broadcastAnnouncement(String subject, String content, String senderUsername);
}
