package com.rescue.system.service;

import com.rescue.system.dto.request.SendMessageRequest;
import com.rescue.system.dto.request.UpdateCostRequest;
import com.rescue.system.dto.response.ConversationDto;
import com.rescue.system.dto.response.MessageDto;

import java.util.List;

/**
 * Service interface for messaging functionality
 * Based on UC101 - Messaging feature
 */
public interface MessageService {

    /**
     * UC101 - Step 3: Send a message
     * Gửi tin nhắn trong cuộc hội thoại
     *
     * @param senderId ID of the sender
     * @param request  Message request containing content and request ID
     * @return The sent message DTO
     */
    MessageDto sendMessage(Long senderId, SendMessageRequest request);

    /**
     * UC101 - Step 2: Get message history
     * Lấy lịch sử tin nhắn của một yêu cầu cứu hộ
     *
     * @param requestId ID of the rescue request
     * @param userId    ID of the current user (for read status)
     * @return Conversation DTO with messages
     */
    ConversationDto getMessagesByRequestId(Long requestId, Long userId);

    /**
     * Get or create a conversation for a rescue request
     * Tạo hoặc lấy cuộc hội thoại cho một yêu cầu cứu hộ
     *
     * @param requestId ID of the rescue request
     * @param userId    ID of the user initiating
     * @return Conversation DTO
     */
    ConversationDto getOrCreateConversation(Long requestId, Long userId);

    /**
     * UC101 - Step 6 & 7: Update agreed cost
     * Cập nhật chi phí đã thỏa thuận
     *
     * @param userId  ID of the user updating
     * @param request Cost update request
     * @return Updated conversation DTO
     */
    ConversationDto updateAgreedCost(Long userId, UpdateCostRequest request);

    /**
     * Mark messages as read
     * Đánh dấu tin nhắn đã đọc
     *
     * @param conversationId ID of the conversation
     * @param userId         ID of the user reading
     * @return Number of messages marked as read
     */
    int markMessagesAsRead(Long conversationId, Long userId);

    /**
     * Get all conversations for a user
     * Lấy danh sách tất cả cuộc hội thoại của người dùng
     *
     * @param userId ID of the user
     * @return List of conversation DTOs
     */
    List<ConversationDto> getUserConversations(Long userId);

    /**
     * End a conversation
     * UC101 - Alternative flow 7a: End use case if no agreement
     *
     * @param conversationId ID of the conversation
     * @param userId         ID of the user ending
     * @param agreed         Whether cost was agreed or not
     * @return Updated conversation DTO
     */
    ConversationDto endConversation(Long conversationId, Long userId, boolean agreed);

    /**
     * Get unread message count for a user
     * Đếm số tin nhắn chưa đọc
     *
     * @param userId ID of the user
     * @return Total unread count across all conversations
     */
    int getUnreadCount(Long userId);
}
