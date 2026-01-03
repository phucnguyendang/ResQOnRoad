package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateReviewRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.ReviewDetail;
import com.rescue.system.exception.ApiException;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReviewController - Xử lý các API liên quan đến đánh giá và phản hồi (UC102)
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * UC102: Tạo hoặc cập nhật đánh giá
     * POST /api/reviews
     * 
     * Request body:
     * {
     *   "requestId": 1001,
     *   "rating": 5,
     *   "comment": "Tới nhanh, xử lý chuyên nghiệp."
     * }
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewDetail>> createOrUpdateReview(
            @Valid @RequestBody CreateReviewRequest reviewRequest,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            ReviewDetail result = reviewService.createOrUpdateReview(userId, reviewRequest);

            ApiResponse<ReviewDetail> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Gửi đánh giá thành công",
                    result
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ApiException e) {
            return buildErrorResponse(e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * UC102: Lấy danh sách đánh giá của công ty (có phân trang)
     * GET /api/companies/{companyId}/reviews?page=1&limit=10
     */
    @GetMapping("/companies/{companyId}/reviews")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCompanyReviews(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(page - 1, limit);
            Page<ReviewDetail> reviewPage = reviewService.getCompanyReviews(companyId, pageable);

            Map<String, Object> data = new HashMap<>();
            data.put("items", reviewPage.getContent());
            data.put("pagination", new HashMap<String, Object>() {{
                put("current_page", page);
                put("total_pages", reviewPage.getTotalPages());
                put("total_items", reviewPage.getTotalElements());
            }});

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                    "Lấy danh sách đánh giá thành công",
                    data
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return buildErrorResponse(e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * UC102: Lấy điểm đánh giá trung bình của công ty
     * GET /api/companies/{companyId}/rating
     */
    @GetMapping("/companies/{companyId}/rating")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCompanyAverageRating(
            @PathVariable Long companyId) {
        try {
            Double averageRating = reviewService.getCompanyAverageRating(companyId);

            Map<String, Object> data = new HashMap<>();
            data.put("company_id", companyId);
            data.put("rating_avg", averageRating);

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                    "Lấy điểm đánh giá thành công",
                    data
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return buildErrorResponse(e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * UC102: Lấy danh sách đánh giá của người dùng hiện tại
     * GET /api/reviews/my-reviews
     */
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ReviewDetail>>> getMyReviews(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            List<ReviewDetail> reviews = reviewService.getUserReviews(userId);

            ApiResponse<List<ReviewDetail>> response = new ApiResponse<>(
                    "Lấy danh sách đánh giá của bạn thành công",
                    reviews
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return buildErrorResponse(e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * UC102: Kiểm tra xem người dùng đã đánh giá công ty này chưa
     * GET /api/reviews/check?companyId={companyId}
     */
    @GetMapping("/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkIfReviewed(
            @RequestParam Long companyId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            boolean hasReviewed = reviewService.hasUserReviewedCompany(userId, companyId);

            Map<String, Object> data = new HashMap<>();
            data.put("company_id", companyId);
            data.put("has_reviewed", hasReviewed);

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                    "Kiểm tra trạng thái đánh giá thành công",
                    data
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return buildErrorResponse(e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Helper method để lấy userId từ JWT token
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token format");
        }
        
        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
        
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot extract user ID from token");
        }
        
        return userId;
    }

    /**
     * Helper method để build error response
     */
    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(status.value());
        response.setMessage(message);
        return ResponseEntity.status(status).body(response);
    }
}
