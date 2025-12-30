package com.rescue.system.repository;

import com.rescue.system.entity.Conversation;
import com.rescue.system.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for Message entity
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all messages in a conversation ordered by sent time
     */
    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);

    /**
     * Find all messages in a conversation by conversation ID
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt ASC")
    List<Message> findByConversationId(@Param("conversationId") Long conversationId);

    /**
     * Find unread messages for a user in a conversation
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.sender.id != :userId AND m.isRead = false")
    List<Message> findUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    /**
     * Count unread messages for a user in a conversation
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.sender.id != :userId AND m.isRead = false")
    int countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    /**
     * Mark all messages as read for a user in a conversation
     */
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation.id = :conversationId AND m.sender.id != :userId AND m.isRead = false")
    int markMessagesAsRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    /**
     * Find latest message in a conversation
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt DESC LIMIT 1")
    Message findLatestMessage(@Param("conversationId") Long conversationId);
}
