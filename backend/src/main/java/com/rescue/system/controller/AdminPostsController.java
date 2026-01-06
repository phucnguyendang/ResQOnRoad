package com.rescue.system.controller;

import com.rescue.system.service.ContentModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for admin post management
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 * 
 * Provides endpoint:
 * - DELETE /api/admin/posts/{id} - Xóa bài viết/đánh giá vi phạm
 * 
 * This controller is created to match the API specification in
 * implementation_plan.md
 */
@RestController
@RequestMapping("/api/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostsController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPostsController.class);

    private final ContentModerationService moderationService;

    public AdminPostsController(ContentModerationService moderationService) {
        this.moderationService = moderationService;
    }

    /**
     * DELETE /api/admin/posts/{id}
     * Delete content permanently (as specified in the API requirements)
     * (Xóa bài viết/đánh giá vi phạm)
     * 
     * @param id          the content ID
     * @param userDetails authenticated admin details
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String adminUsername = userDetails.getUsername();
        logger.info("Admin {} deleting post/content {} permanently via /api/admin/posts endpoint",
                adminUsername, id);

        moderationService.deleteContent(id, adminUsername);
        return ResponseEntity.ok(Map.of(
                "message", "Content deleted successfully",
                "id", id.toString()));
    }
}
