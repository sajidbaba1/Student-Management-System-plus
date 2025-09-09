package net.javaguides.sms.repository;

import net.javaguides.sms.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findBySessionIdOrderByTimestampAsc(String sessionId);
    
    List<ChatMessage> findByUsernameOrderByTimestampDesc(String username);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.timestamp >= :startDate ORDER BY cm.timestamp DESC")
    List<ChatMessage> findRecentMessages(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.resolved = false ORDER BY cm.timestamp DESC")
    List<ChatMessage> findUnresolvedMessages();
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.timestamp >= :startDate")
    Long countMessagesSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT cm.intent, COUNT(cm) FROM ChatMessage cm WHERE cm.intent IS NOT NULL GROUP BY cm.intent ORDER BY COUNT(cm) DESC")
    List<Object[]> findTopIntents();
}
