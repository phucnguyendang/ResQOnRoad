package com.rescue.system.service.impl;

import com.rescue.system.dto.request.SendMessageRequest;
import com.rescue.system.dto.request.UpdateCostRequest;
import com.rescue.system.dto.response.ConversationDto;
import com.rescue.system.dto.response.MessageDto;
import com.rescue.system.entity.*;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.ConversationRepository;
import com.rescue.system.repository.MessageRepository;
import com.rescue.system.repository.RescueRequestRepository;
import com.rescue.system.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of MessageService
 * Handles all messaging functionality for UC101
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private RescueRequestRepository rescueRequestRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public MessageDto sendMessage(Long senderId, SendMessageRequest request) {
        // Get sender account
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        // Get rescue request
        RescueRequest rescueRequest = rescueRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu cứu hộ"));

        // Validate sender is part of this request
        validateUserInRequest(sender, rescueRequest);

        // Get or create conversation
        Conversation conversation = getOrCreateConversationEntity(rescueRequest, sender);

        // Check if conversation is still active
        if (conversation.getStatus() != ConversationStatus.ACTIVE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cuộc hội thoại đã kết thúc");
        }

        // Create and save message
        Message message = new Message(conversation, sender, request.getContent());
        if (request.getAttachmentUrl() != null) {
            message.setAttachmentUrl(request.getAttachmentUrl());
            message.setAttachmentType(request.getAttachmentType());
        }

        message = messageRepository.save(message);

        return convertToMessageDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDto getMessagesByRequestId(Long requestId, Long userId) {
        // Find conversation
        Conversation conversation = conversationRepository.findByRescueRequestId(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc hội thoại"));

        // Validate user is part of conversation
        validateUserInConversation(userId, conversation);

        // Mark messages as read
        messageRepository.markMessagesAsRead(conversation.getId(), userId);

        // Get all messages
        List<Message> messages = messageRepository.findByConversationId(conversation.getId());

        return convertToConversationDto(conversation, messages, userId);
    }

    @Override
    public ConversationDto getOrCreateConversation(Long requestId, Long userId) {
        // Get user account
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        // Get rescue request
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu cứu hộ"));

        // Validate user is part of this request
        validateUserInRequest(user, rescueRequest);

        // Get or create conversation
        Conversation conversation = getOrCreateConversationEntity(rescueRequest, user);

        List<Message> messages = messageRepository.findByConversationId(conversation.getId());
        return convertToConversationDto(conversation, messages, userId);
    }

    @Override
    public ConversationDto updateAgreedCost(Long userId, UpdateCostRequest request) {
        // Find conversation
        Conversation conversation = conversationRepository.findByRescueRequestId(request.getRequestId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc hội thoại"));

        // Validate user is part of conversation
        validateUserInConversation(userId, conversation);

        // Update cost
        conversation.setAgreedCost(request.getAgreedCost());
        conversation.setCostNotes(request.getCostNotes());
        conversation.setStatus(ConversationStatus.COST_AGREED);

        conversation = conversationRepository.save(conversation);

        // Also update the rescue request with the agreed cost if needed
        RescueRequest rescueRequest = conversation.getRescueRequest();
        // You might want to add an agreedCost field to RescueRequest entity

        List<Message> messages = messageRepository.findByConversationId(conversation.getId());
        return convertToConversationDto(conversation, messages, userId);
    }

    @Override
    public int markMessagesAsRead(Long conversationId, Long userId) {
        // Validate conversation exists
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc hội thoại"));

        // Validate user is part of conversation
        validateUserInConversation(userId, conversation);

        return messageRepository.markMessagesAsRead(conversationId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUserIdOrCompanyId(userId);

        return conversations.stream()
                .map(conv -> {
                    List<Message> messages = messageRepository.findByConversationId(conv.getId());
                    return convertToConversationDto(conv, messages, userId);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ConversationDto endConversation(Long conversationId, Long userId, boolean agreed) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc hội thoại"));

        // Validate user is part of conversation
        validateUserInConversation(userId, conversation);

        // Update status based on agreement
        if (agreed) {
            conversation.setStatus(ConversationStatus.COST_AGREED);
        } else {
            conversation.setStatus(ConversationStatus.COST_DISAGREED);
        }
        conversation.setEndedAt(Instant.now());

        conversation = conversationRepository.save(conversation);

        List<Message> messages = messageRepository.findByConversationId(conversation.getId());
        return convertToConversationDto(conversation, messages, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUserIdOrCompanyId(userId);

        int totalUnread = 0;
        for (Conversation conversation : conversations) {
            totalUnread += messageRepository.countUnreadMessages(conversation.getId(), userId);
        }

        return totalUnread;
    }

    // Helper methods

    private Conversation getOrCreateConversationEntity(RescueRequest rescueRequest, Account initiator) {
        return conversationRepository.findByRescueRequest(rescueRequest)
                .orElseGet(() -> {
                    // Validate request has both user and company
                    if (rescueRequest.getUser() == null) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu chưa có người dùng");
                    }
                    if (rescueRequest.getCompany() == null) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu chưa được gán cho đơn vị cứu hộ");
                    }

                    Conversation newConversation = new Conversation(
                            rescueRequest,
                            rescueRequest.getUser(),
                            rescueRequest.getCompany());
                    return conversationRepository.save(newConversation);
                });
    }

    private void validateUserInRequest(Account user, RescueRequest request) {
        boolean isUser = request.getUser() != null && request.getUser().getId().equals(user.getId());
        boolean isCompany = request.getCompany() != null && request.getCompany().getId().equals(user.getId());

        if (!isUser && !isCompany) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập cuộc hội thoại này");
        }
    }

    private void validateUserInConversation(Long userId, Conversation conversation) {
        boolean isUser = conversation.getUser().getId().equals(userId);
        boolean isCompany = conversation.getCompany().getId().equals(userId);

        if (!isUser && !isCompany) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập cuộc hội thoại này");
        }
    }

    private MessageDto convertToMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName() != null
                ? message.getSender().getFullName()
                : message.getSender().getUsername());
        dto.setSenderRole(message.getSender().getRole().name());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setIsRead(message.getIsRead());
        dto.setAttachmentUrl(message.getAttachmentUrl());
        dto.setAttachmentType(message.getAttachmentType());
        return dto;
    }

    private ConversationDto convertToConversationDto(Conversation conversation, List<Message> messages,
            Long currentUserId) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setRequestId(conversation.getRescueRequest().getId());
        dto.setUserId(conversation.getUser().getId());
        dto.setUserName(conversation.getUser().getFullName() != null
                ? conversation.getUser().getFullName()
                : conversation.getUser().getUsername());
        dto.setCompanyId(conversation.getCompany().getId());
        dto.setCompanyName(conversation.getCompany().getFullName() != null
                ? conversation.getCompany().getFullName()
                : conversation.getCompany().getUsername());
        dto.setStartedAt(conversation.getStartedAt());
        dto.setEndedAt(conversation.getEndedAt());
        dto.setStatus(conversation.getStatus());
        dto.setAgreedCost(conversation.getAgreedCost());
        dto.setCostNotes(conversation.getCostNotes());

        // Convert messages
        List<MessageDto> messageDtos = messages.stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());
        dto.setMessages(messageDtos);

        // Count unread messages
        int unreadCount = messageRepository.countUnreadMessages(conversation.getId(), currentUserId);
        dto.setUnreadCount(unreadCount);

        return dto;
    }
}
