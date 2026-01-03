package com.rescue.system.service;

import com.rescue.system.dto.request.CreateReviewRequest;
import com.rescue.system.dto.response.ReviewDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    /**
     * UC102: Tạo hoặc cập nhật đánh giá
     * @param userId ID của người dùng
     * @param reviewRequest Request chứa thông tin đánh giá
     * @return ReviewDetail sau khi tạo/cập nhật
     */
    ReviewDetail createOrUpdateReview(Long userId, CreateReviewRequest reviewRequest);

    /**
     * Lấy danh sách đánh giá của công ty (có phân trang)
     * @param companyId ID của công ty
     * @param pageable Thông tin phân trang
     * @return Page của ReviewDetail
     */
    Page<ReviewDetail> getCompanyReviews(Long companyId, Pageable pageable);

    /**
     * Lấy đánh giá trung bình của công ty
     * @param companyId ID của công ty
     * @return Điểm đánh giá trung bình (0 - 5)
     */
    Double getCompanyAverageRating(Long companyId);

    /**
     * Lấy danh sách đánh giá của người dùng
     * @param userId ID của người dùng
     * @return Danh sách ReviewDetail
     */
    List<ReviewDetail> getUserReviews(Long userId);

    /**
     * Kiểm tra xem người dùng đã đánh giá công ty này chưa
     * @param userId ID của người dùng
     * @param companyId ID của công ty
     * @return true nếu đã đánh giá, false nếu chưa
     */
    boolean hasUserReviewedCompany(Long userId, Long companyId);
}
