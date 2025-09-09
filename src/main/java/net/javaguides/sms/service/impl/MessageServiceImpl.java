package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Message;
import net.javaguides.sms.repository.MessageRepository;
import net.javaguides.sms.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getInboxMessages(String username) {
        return messageRepository.findByRecipientUsernameOrderBySentAtDesc(username);
    }

    @Override
    public List<Message> getSentMessages(String username) {
        return messageRepository.findBySenderUsernameOrderBySentAtDesc(username);
    }

    @Override
    public List<Message> getUnreadMessages(String username) {
        return messageRepository.findByRecipientUsernameAndIsReadFalse(username);
    }

    @Override
    public List<Message> getAnnouncements() {
        return messageRepository.findByMessageTypeOrderBySentAtDesc("ANNOUNCEMENT");
    }

    @Override
    public long getUnreadCount(String username) {
        return messageRepository.countByRecipientUsernameAndIsReadFalse(username);
    }

    @Transactional
    @Override
    public Message markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        message.markAsRead();
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    @Override
    public Page<Message> getAllMessages(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    @Override
    public Message broadcastAnnouncement(String subject, String content, String senderUsername) {
        Message announcement = new Message(senderUsername, "ALL", subject, content, "ANNOUNCEMENT");
        return messageRepository.save(announcement);
    }
}
