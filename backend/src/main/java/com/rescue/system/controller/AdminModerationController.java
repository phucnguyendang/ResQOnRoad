package com.rescue.system.controller;

import com.rescue.system.dto.request.ModerationActionRequest;
import com.rescue.system.dto.response.ModeratableContentResponse;
import com.rescue.system.dto.response.ModerationListResponse;
import com.rescue.system.entity.ContentStatus;
import com.rescue.system.entity.ContentType;
import com.rescue.system.service.ContentModerationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for admin content moderation
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 * 
 * Provides endpoints for admins to:
 * - View list of content pending moderation
 * - View content details
 * - Approve, reject, or remove content
 * - Delete content permanently
 */
@RestController
@RequestMapping("/api/admin/moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminModerationController {

    private static final Logger logger = LoggerFactory.getLogger(AdminModerationController.class);

    private final ContentModerationService moderationService;

    public AdminModerationController(ContentModerationService moderationService) {
        this.moderationService = moderationService;
    }

    /**
     * GET /api/admin/moderation
     * Get list of all content for moderation with optional filters
     * (Hiển thị danh sách nội dung chờ duyệt)
     * 
     * @param status filter by status (PENDING, APPROVED, REJECTED, REMOVED)
     * @param type   filter by content type (POST, COMMENT, REVIEW)
     * @param page   page number (0-indexed)
     * @param size   page size (default: 20)
     * @return paginated list of content
     */
    @GetMapping
    public ResponseEntity<ModerationListResponse> getModerationList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Admin fetching moderation list - status: {}, type: {}, page: {}, size: {}",
                status, type, page, size);

        ContentStatus contentStatus = parseContentStatus(status);
        ContentType contentType = parseContentType(type);

        ModerationListResponse response = moderationService.getContentList(contentStatus, contentType, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/moderation/pending
     * Get list of pending content (shortcut for status=PENDING)
     * (Lấy danh sách nội dung đang chờ duyệt)
     * 
     * @param page page number
     * @param size page size
     * @return paginated list of pending content
     */
    @GetMapping("/pending")
    public ResponseEntity<ModerationListResponse> getPendingContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Admin fetching pending content - page: {}, size: {}", page, size);
        ModerationListResponse response = moderationService.getPendingContent(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/moderation/statistics
     * Get moderation statistics
     * (Lấy thống kê kiểm duyệt)
     * 
     * @return statistics about content moderation
     */
    @GetMapping("/statistics")
    public ResponseEntity<ModerationListResponse> getStatistics() {
        logger.info("Admin fetching moderation statistics");
        ModerationListResponse response = moderationService.getStatistics();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/moderation/search
     * Search content by keyword
     * (Tìm kiếm nội dung)
     * 
     * @param keyword search keyword
     * @param status  filter by status
     * @param page    page number
     * @param size    page size
     * @return search results
     */
    @GetMapping("/search")
    public ResponseEntity<ModerationListResponse> searchContent(
            @RequestParam String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Admin searching content - keyword: {}, status: {}", keyword, status);
        ContentStatus contentStatus = parseContentStatus(status);
        ModerationListResponse response = moderationService.searchContent(keyword, contentStatus, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/moderation/{id}
     * Get detailed information of a specific content
     * (Xem chi tiết một nội dung)
     * 
     * @param id the content ID
     * @return content details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModeratableContentResponse> getContentDetail(@PathVariable Long id) {
        logger.info("Admin viewing content detail - id: {}", id);
        ModeratableContentResponse response = moderationService.getContentDetail(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/admin/moderation/{id}
     * Process moderation action (approve, reject, or remove)
     * (Xử lý kiểm duyệt: Phê duyệt, Từ chối, hoặc Gỡ bỏ nội dung)
     * 
     * @param id          the content ID
     * @param request     the moderation action request
     * @param userDetails authenticated admin details
     * @return updated content
     */
    @PutMapping("/{id}")
    public ResponseEntity<ModeratableContentResponse> processModeration(
            @PathVariable Long id,
            @Valid @RequestBody ModerationActionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String adminUsername = userDetails.getUsername();
        logger.info("Admin {} processing moderation for content {} - action: {}",
                adminUsername, id, request.getAction());

        ModeratableContentResponse response = moderationService.processModeration(id, request, adminUsername);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/admin/moderation/{id}/approve
     * Approve content
     * (Phê duyệt nội dung)
     * 
     * @param id          the content ID
     * @param userDetails authenticated admin details
     * @return updated content
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ModeratableContentResponse> approveContent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String adminUsername = userDetails.getUsername();
        logger.info("Admin {} approving content {}", adminUsername, id);

        ModeratableContentResponse response = moderationService.approveContent(id, adminUsername);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/admin/moderation/{id}/reject
     * Reject content
     * (Từ chối nội dung)
     * 
     * @param id          the content ID
     * @param request     contains the reason for rejection
     * @param userDetails authenticated admin details
     * @return updated content
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ModeratableContentResponse> rejectContent(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String adminUsername = userDetails.getUsername();
        String reason = request.get("reason");
        logger.info("Admin {} rejecting content {} with reason: {}", adminUsername, id, reason);

        ModeratableContentResponse response = moderationService.rejectContent(id, reason, adminUsername);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/admin/moderation/{id}/remove
     * Remove content (for approved content that violates rules)
     * (Gỡ bỏ nội dung vi phạm)
     * 
     * @param id          the content ID
     * @param request     contains the reason for removal
     * @param userDetails authenticated admin details
     * @return updated content
     */
    @PostMapping("/{id}/remove")
    public ResponseEntity<ModeratableContentResponse> removeContent(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String adminUsername = userDetails.getUsername();
        String reason = request.get("reason");
        logger.info("Admin {} removing content {} with reason: {}", adminUsername, id, reason);

        ModeratableContentResponse response = moderationService.removeContent(id, reason, adminUsername);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/admin/moderation/posts/{id}
     * Delete content permanently (alternative endpoint within moderation module)
     * (Xóa bài viết/đánh giá vi phạm - endpoint phụ)
     * 
     * Note: The primary endpoint as per API specification is DELETE
     * /api/admin/posts/{id}
     * which is handled by AdminPostsController
     * 
     * @param id          the content ID
     * @param userDetails authenticated admin details
     * @return success message
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String adminUsername = userDetails.getUsername();
        logger.info("Admin {} deleting content {} permanently", adminUsername, id);

        moderationService.deleteContent(id, adminUsername);
        return ResponseEntity.ok(Map.of(
                "message", "Content deleted successfully",
                "id", id.toString()));
    }

    /**
     * DELETE /api/admin/moderation/{id}
     * Delete content permanently (alternative endpoint)
     * (Xóa nội dung vĩnh viễn)
     * 
     * @param id          the content ID
     * @param userDetails authenticated admin details
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteContent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String adminUsername = userDetails.getUsername();
        logger.info("Admin {} deleting content {} permanently", adminUsername, id);

        moderationService.deleteContent(id, adminUsername);
        return ResponseEntity.ok(Map.of(
                "message", "Content deleted successfully",
                "id", id.toString()));
    }

    // ==================== Helper Methods ====================

    private ContentStatus parseContentStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return ContentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid content status: {}", status);
            return null;
        }
    }

    private ContentType parseContentType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        try {
            return ContentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid content type: {}", type);
            return null;
        }
    }
}
