package com.rescue.system.controller;

import com.rescue.system.dto.request.SendMessageRequest;
import com.rescue.system.dto.request.UpdateCostRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.ConversationDto;
import com.rescue.system.dto.response.MessageDto;
import com.rescue.system.exception.ApiException;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.MessageService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for messaging functionality
 * UC101 - Nhắn tin (Messaging)
 * 
 * APIs:
 * - POST /api/messages - Send a message
 * - GET /api/messages/{requestId} - Get messages for a rescue request
 * - POST /api/messages/conversation/{requestId} - Get or create conversation
 * - PUT /api/messages/cost - Update agreed cost
 * - PUT /api/messages/{conversationId}/read - Mark messages as read
 * - GET /api/messages/conversations - Get all user conversations
 * - PUT /api/messages/conversation/{conversationId}/end - End conversation
 * - GET /api/messages/unread-count - Get unread message count
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * UC101 - Step 3: Send a message
     * POST /api/messages
     * 
     * Gửi tin nhắn trong cuộc hội thoại
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<MessageDto>> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            MessageDto result = messageService.sendMessage(userId, request);

            ApiResponse<MessageDto> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Tin nhắn đã được gửi thành công",
                    result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi gửi tin nhắn: " + e.getMessage());
        }
    }

    /**
     * UC101 - Step 2: Get message history
     * GET /api/messages/{requestId}
     * 
     * Lấy lịch sử tin nhắn của một yêu cầu cứu hộ
     */
    @GetMapping("/{requestId}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<ConversationDto>> getMessages(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            ConversationDto result = messageService.getMessagesByRequestId(requestId, userId);

            ApiResponse<ConversationDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy lịch sử tin nhắn thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy tin nhắn: " + e.getMessage());
        }
    }

    /**
     * UC101 - Step 1: Access messaging interface
     * POST /api/messages/conversation/{requestId}
     * 
     * Tạo hoặc lấy cuộc hội thoại cho một yêu cầu cứu hộ
     */
    @PostMapping("/conversation/{requestId}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<ConversationDto>> getOrCreateConversation(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            ConversationDto result = messageService.getOrCreateConversation(requestId, userId);

            ApiResponse<ConversationDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Truy cập cuộc hội thoại thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi truy cập cuộc hội thoại: " + e.getMessage());
        }
    }

    /**
     * UC101 - Step 6 & 7: Update agreed cost
     * PUT /api/messages/cost
     * 
     * Ghi nhận chi phí đã thỏa thuận
     */
    @PutMapping("/cost")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<ConversationDto>> updateAgreedCost(
            @Valid @RequestBody UpdateCostRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            ConversationDto result = messageService.updateAgreedCost(userId, request);

            ApiResponse<ConversationDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Đã ghi nhận chi phí thỏa thuận",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi cập nhật chi phí: " + e.getMessage());
        }
    }

    /**
     * Mark messages as read
     * PUT /api/messages/{conversationId}/read
     * 
     * Đánh dấu tin nhắn đã đọc
     */
    @PutMapping("/{conversationId}/read")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<Integer>> markMessagesAsRead(
            @PathVariable Long conversationId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            int count = messageService.markMessagesAsRead(conversationId, userId);

            ApiResponse<Integer> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Đã đánh dấu " + count + " tin nhắn đã đọc",
                    count);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi đánh dấu tin nhắn: " + e.getMessage());
        }
    }

    /**
     * Get all conversations for the current user
     * GET /api/messages/conversations
     * 
     * Lấy danh sách tất cả cuộc hội thoại của người dùng
     */
    @GetMapping("/conversations")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<List<ConversationDto>>> getUserConversations(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            List<ConversationDto> result = messageService.getUserConversations(userId);

            ApiResponse<List<ConversationDto>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách cuộc hội thoại thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách cuộc hội thoại: " + e.getMessage());
        }
    }

    /**
     * End a conversation
     * PUT /api/messages/conversation/{conversationId}/end
     * 
     * UC101 - Alternative flow 7a: End use case if no agreement
     */
    @PutMapping("/conversation/{conversationId}/end")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<ConversationDto>> endConversation(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "false") boolean agreed,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            ConversationDto result = messageService.endConversation(conversationId, userId, agreed);

            String message = agreed
                    ? "Cuộc hội thoại đã kết thúc với thỏa thuận"
                    : "Cuộc hội thoại đã kết thúc không có thỏa thuận";

            ApiResponse<ConversationDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    message,
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi kết thúc cuộc hội thoại: " + e.getMessage());
        }
    }

    /**
     * Get unread message count for the current user
     * GET /api/messages/unread-count
     * 
     * Đếm số tin nhắn chưa đọc
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            int count = messageService.getUnreadCount(userId);

            ApiResponse<Integer> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy số tin nhắn chưa đọc thành công",
                    count);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi đếm tin nhắn chưa đọc: " + e.getMessage());
        }
    }

    /**
     * Extract user ID from JWT token
     */
    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtTokenProvider.parseClaims(token);
            return claims.get("account_id", Long.class);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn");
        }
    }
}
