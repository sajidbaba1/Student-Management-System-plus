package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientUsernameOrderBySentAtDesc(String recipientUsername);
    List<Message> findBySenderUsernameOrderBySentAtDesc(String senderUsername);
    List<Message> findByRecipientUsernameAndIsReadFalse(String recipientUsername);
    List<Message> findByMessageTypeOrderBySentAtDesc(String messageType);
    long countByRecipientUsernameAndIsReadFalse(String recipientUsername);
}
