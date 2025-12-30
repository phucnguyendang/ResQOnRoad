package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateCommunityCommentRequest;
import com.rescue.system.dto.request.CreateCommunityPostRequest;
import com.rescue.system.dto.request.UpdateCommunityPostRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.CommunityCommentDto;
import com.rescue.system.dto.response.CommunityPostDto;
import com.rescue.system.exception.ApiException;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Community Support features
 * UC103: Community Support and Consultation (Hỗ trợ và tư vấn từ cộng đồng)
 * 
 * API Endpoints:
 * - POST /api/community/posts - Create a new post
 * - GET /api/community/posts - Get all posts (paginated)
 * - GET /api/community/posts/{id} - Get post by ID
 * - PUT /api/community/posts/{id} - Update a post
 * - DELETE /api/community/posts/{id} - Delete a post
 * - PATCH /api/community/posts/{id}/resolve - Mark post as resolved
 * - POST /api/community/posts/{id}/comments - Add comment to a post
 * - GET /api/community/posts/{id}/comments - Get comments for a post
 * - GET /api/community/posts/search - Search posts
 * - GET /api/community/posts/nearby - Get nearby posts
 * - GET /api/community/posts/unresolved - Get unresolved posts
 * - GET /api/community/posts/popular - Get popular posts
 * - GET /api/community/posts/my-posts - Get current user's posts
 * - PUT /api/community/comments/{id} - Update a comment
 * - DELETE /api/community/comments/{id} - Delete a comment
 * - POST /api/community/comments/{id}/helpful - Mark comment as helpful
 * - POST /api/community/comments/{id}/upvote - Upvote a comment
 */
@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ==================== POST ENDPOINTS ====================

    /**
     * UC103 - Step 1: Create a new community post
     * POST /api/community/posts
     */
    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<CommunityPostDto>> createPost(
            @Valid @RequestBody CreateCommunityPostRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            CommunityPostDto result = communityService.createPost(userId, request);

            ApiResponse<CommunityPostDto> response = new ApiResponse<>(
                    "Đăng bài thành công",
                    result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo bài đăng: " + e.getMessage());
        }
    }

    /**
     * UC103 - Step 2: Get all posts for community feed
     * GET /api/community/posts
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<CommunityPostDto>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommunityPostDto> result = communityService.getAllPosts(pageable);

            ApiResponse<Page<CommunityPostDto>> response = new ApiResponse<>(
                    "Lấy danh sách bài đăng thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể lấy danh sách bài đăng: " + e.getMessage());
        }
    }

    /**
     * Get post by ID with view count increment
     * GET /api/community/posts/{id}
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<CommunityPostDto>> getPostById(@PathVariable Long id) {
        try {
            CommunityPostDto result = communityService.getPostByIdAndIncrementView(id);

            ApiResponse<CommunityPostDto> response = new ApiResponse<>(
                    "Lấy chi tiết bài đăng thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể lấy chi tiết bài đăng: " + e.getMessage());
        }
    }

    /**
     * Update a post
     * PUT /api/community/posts/{id}
     */
    @PutMapping("/posts/{id}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<CommunityPostDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommunityPostRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            CommunityPostDto result = communityService.updatePost(id, userId, request);

            ApiResponse<CommunityPostDto> response = new ApiResponse<>(
                    "Cập nhật bài đăng thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể cập nhật bài đăng: " + e.getMessage());
        }
    }

    /**
     * Delete a post
     * DELETE /api/community/posts/{id}
     */
    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            communityService.deletePost(id, userId);

            ApiResponse<Void> response = new ApiResponse<>(
                    "Xóa bài đăng thành công",
                    null);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xóa bài đăng: " + e.getMessage());
        }
    }

    /**
     * UC103 - Step 5: Mark post as resolved
     * PATCH /api/community/posts/{id}/resolve
     */
    @PatchMapping("/posts/{id}/resolve")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<CommunityPostDto>> markPostAsResolved(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            CommunityPostDto result = communityService.markPostAsResolved(id, userId);

            ApiResponse<CommunityPostDto> response = new ApiResponse<>(
                    "Đã đánh dấu bài đăng là đã giải quyết",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể đánh dấu bài đăng: " + e.getMessage());
        }
    }

    /**
     * Search posts by keyword
     * GET /api/community/posts/search
     */
    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponse<Page<CommunityPostDto>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommunityPostDto> result = communityService.searchPosts(keyword, pageable);

            ApiResponse<Page<CommunityPostDto>> response = new ApiResponse<>(
                    "Tìm kiếm bài đăng thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tìm kiếm bài đăng: " + e.getMessage());
        }
    }

    /**
     * Get nearby posts by location
     * GET /api/community/posts/nearby
     */
    @GetMapping("/posts/nearby")
    public ResponseEntity<ApiResponse<List<CommunityPostDto>>> getNearbyPosts(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        try {
            List<CommunityPostDto> result = communityService.getNearbyPosts(latitude, longitude, radiusKm);

            ApiResponse<List<CommunityPostDto>> response = new ApiResponse<>(
                    "Lấy danh sách bài đăng gần đây thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể lấy bài đăng gần đây: " + e.getMessage());
        }
    }

    /**
     * Get unresolved posts (need help)
     * GET /api/community/posts/unresolved
     */
    @GetMapping("/posts/unresolved")
    public ResponseEntity<ApiResponse<List<CommunityPostDto>>> getUnresolvedPosts() {
        try {
            List<CommunityPostDto> result = communityService.getUnresolvedPosts();

            ApiResponse<List<CommunityPostDto>> response = new ApiResponse<>(
                    "Lấy danh sách bài đăng chưa giải quyết thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lấy danh sách: " + e.getMessage());
        }
    }

    /**
     * Get popular posts
     * GET /api/community/posts/popular
     */
    @GetMapping("/posts/popular")
    public ResponseEntity<ApiResponse<List<CommunityPostDto>>> getPopularPosts() {
        try {
            List<CommunityPostDto> result = communityService.getPopularPosts();

            ApiResponse<List<CommunityPostDto>> response = new ApiResponse<>(
                    "Lấy danh sách bài đăng phổ biến thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lấy danh sách: " + e.getMessage());
        }
    }

    /**
     * Get current user's posts
     * GET /api/community/posts/my-posts
     */
    @GetMapping("/posts/my-posts")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<Page<CommunityPostDto>>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            Pageable pageable = PageRequest.of(page, size);
            Page<CommunityPostDto> result = communityService.getPostsByAuthor(userId, pageable);

            ApiResponse<Page<CommunityPostDto>> response = new ApiResponse<>(
                    "Lấy danh sách bài đăng của bạn thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lấy danh sách: " + e.getMessage());
        }
    }

    // ==================== COMMENT ENDPOINTS ====================

    /**
     * UC103 - Step 3: Add comment to a post (community provides advice)
     * POST /api/community/posts/{postId}/comments
     */
    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<CommunityCommentDto>> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommunityCommentRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            CommunityCommentDto result = communityService.addComment(postId, userId, request);

            ApiResponse<CommunityCommentDto> response = new ApiResponse<>(
                    "Thêm bình luận thành công",
                    result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể thêm bình luận: " + e.getMessage());
        }
    }

    /**
     * UC103 - Step 4: Get comments for a post (display community advice)
     * GET /api/community/posts/{postId}/comments
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommunityCommentDto>>> getComments(@PathVariable Long postId) {
        try {
            List<CommunityCommentDto> result = communityService.getCommentsByPostId(postId);

            ApiResponse<List<CommunityCommentDto>> response = new ApiResponse<>(
                    "Lấy danh sách bình luận thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể lấy danh sách bình luận: " + e.getMessage());
        }
    }

    /**
     * Get helpful comments for a post
     * GET /api/community/posts/{postId}/comments/helpful
     */
    @GetMapping("/posts/{postId}/comments/helpful")
    public ResponseEntity<ApiResponse<List<CommunityCommentDto>>> getHelpfulComments(@PathVariable Long postId) {
        try {
            List<CommunityCommentDto> result = communityService.getHelpfulComments(postId);

            ApiResponse<List<CommunityCommentDto>> response = new ApiResponse<>(
                    "Lấy danh sách bình luận hữu ích thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lấy danh sách: " + e.getMessage());
        }
    }

    /**
     * Update a comment
     * PUT /api/community/comments/{commentId}
     */
    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<CommunityCommentDto>> updateComment(
            @PathVariable Long commentId,
            @RequestBody String newContent,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            CommunityCommentDto result = communityService.updateComment(commentId, userId, newContent);

            ApiResponse<CommunityCommentDto> response = new ApiResponse<>(
                    "Cập nhật bình luận thành công",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể cập nhật bình luận: " + e.getMessage());
        }
    }

    /**
     * Delete a comment
     * DELETE /api/community/comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            communityService.deleteComment(commentId, userId);

            ApiResponse<Void> response = new ApiResponse<>(
                    "Xóa bình luận thành công",
                    null);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xóa bình luận: " + e.getMessage());
        }
    }

    /**
     * Mark comment as helpful (by post author)
     * POST /api/community/comments/{commentId}/helpful
     */
    @PostMapping("/comments/{commentId}/helpful")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<CommunityCommentDto>> markCommentAsHelpful(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            CommunityCommentDto result = communityService.markCommentAsHelpful(commentId, userId);

            ApiResponse<CommunityCommentDto> response = new ApiResponse<>(
                    "Đã đánh dấu bình luận là hữu ích",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể đánh dấu bình luận: " + e.getMessage());
        }
    }

    /**
     * Upvote a comment (increment helpful count)
     * POST /api/community/comments/{commentId}/upvote
     */
    @PostMapping("/comments/{commentId}/upvote")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY')")
    public ResponseEntity<ApiResponse<CommunityCommentDto>> upvoteComment(@PathVariable Long commentId) {
        try {
            CommunityCommentDto result = communityService.incrementHelpfulCount(commentId);

            ApiResponse<CommunityCommentDto> response = new ApiResponse<>(
                    "Đã vote cho bình luận",
                    result);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể vote bình luận: " + e.getMessage());
        }
    }

    // ==================== HELPER METHODS ====================

    private Long getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtTokenProvider.getUserIdFromJWT(token);
    }
}
