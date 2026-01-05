package com.rescue.system.repository;

import com.rescue.system.entity.Account;
import com.rescue.system.entity.Conversation;
import com.rescue.system.entity.ConversationStatus;
import com.rescue.system.entity.RescueRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Conversation entity
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * Find conversation by rescue request
     */
    Optional<Conversation> findByRescueRequest(RescueRequest rescueRequest);

    /**
     * Find conversation by rescue request ID
     */
    @Query("SELECT c FROM Conversation c WHERE c.rescueRequest.id = :requestId")
    Optional<Conversation> findByRescueRequestId(@Param("requestId") Long requestId);

    /**
     * Find all conversations for a user (as customer)
     */
    List<Conversation> findByUserOrderByStartedAtDesc(Account user);

    /**
     * Find all conversations for a company
     */
    List<Conversation> findByCompanyOrderByStartedAtDesc(Account company);

    /**
     * Find all conversations involving a user (either as user or company)
     */
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId OR c.company.id = :userId ORDER BY c.startedAt DESC")
    List<Conversation> findByUserIdOrCompanyId(@Param("userId") Long userId);

    /**
     * Find active conversations for a user
     */
    @Query("SELECT c FROM Conversation c WHERE (c.user.id = :userId OR c.company.id = :userId) AND c.status = :status ORDER BY c.startedAt DESC")
    List<Conversation> findActiveConversations(@Param("userId") Long userId,
            @Param("status") ConversationStatus status);

    /**
     * Check if a conversation exists for a rescue request
     */
    boolean existsByRescueRequest(RescueRequest rescueRequest);

    /**
     * Check if a conversation exists for a rescue request ID
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Conversation c WHERE c.rescueRequest.id = :requestId")
    boolean existsByRescueRequestId(@Param("requestId") Long requestId);
}
