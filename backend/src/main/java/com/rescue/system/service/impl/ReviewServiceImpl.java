package com.rescue.system.service.impl;

import com.rescue.system.dto.request.CreateReviewRequest;
import com.rescue.system.dto.response.ReviewDetail;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.RescueRequest;
import com.rescue.system.entity.RescueStatus;
import com.rescue.system.entity.Review;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.RescueCompanyRepository;
import com.rescue.system.repository.RescueRequestRepository;
import com.rescue.system.repository.ReviewRepository;
import com.rescue.system.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RescueRequestRepository rescueRequestRepository;

    @Autowired
    private RescueCompanyRepository rescueCompanyRepository;

    @Override
    @Transactional
    public ReviewDetail createOrUpdateReview(Long userId, CreateReviewRequest reviewRequest) {
        // Kiểm tra user tồn tại
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        // Kiểm tra rescue request tồn tại và status là COMPLETED
        RescueRequest rescueRequest = rescueRequestRepository.findById(reviewRequest.getRequestId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Yêu cầu cứu hộ không tồn tại"));

        // Kiểm tra yêu cầu đã hoàn thành
        if (!RescueStatus.COMPLETED.equals(rescueRequest.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                    "Chỉ có thể đánh giá khi yêu cầu cứu hộ đã hoàn thành");
        }

        // Kiểm tra user có phải là người tạo yêu cầu này không
        if (!rescueRequest.getUser().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, 
                    "Bạn không có quyền đánh giá yêu cầu này");
        }

        RescueCompany company = null;
        if (rescueRequest.getCompany() != null) {
            company = rescueCompanyRepository.findById(rescueRequest.getCompany().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Công ty không tồn tại"));
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                    "Yêu cầu này chưa được gán cho công ty nào");
        }

        // Tìm xem đã có đánh giá trước đó không
        List<Review> existingReviews = reviewRepository.findByUserIdAndCompanyId(userId, company.getId());
        
        Review review;
        if (!existingReviews.isEmpty()) {
            // Cập nhật đánh giá cũ
            review = existingReviews.get(0);
            review.setRating(reviewRequest.getRating());
            review.setComment(reviewRequest.getComment());
        } else {
            // Tạo đánh giá mới
            review = new Review();
            review.setUser(user);
            review.setCompany(company);
            review.setRating(reviewRequest.getRating());
            review.setComment(reviewRequest.getComment());
            review.setIsVerified(true); // Đánh giá từ hệ thống được xác thực
        }

        Review savedReview = reviewRepository.save(review);

        return convertToReviewDetail(savedReview, user.getFullName());
    }

    @Override
    public Page<ReviewDetail> getCompanyReviews(Long companyId, Pageable pageable) {
        // Kiểm tra công ty tồn tại
        if (!rescueCompanyRepository.existsById(companyId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Công ty không tồn tại");
        }

        Page<Review> reviewPage = reviewRepository.findByCompanyId(companyId, pageable);
        return reviewPage.map(review -> convertToReviewDetail(review, review.getUser().getFullName()));
    }

    @Override
    public Double getCompanyAverageRating(Long companyId) {
        List<Review> reviews = reviewRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }

        Double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0; // Làm tròn đến 1 chữ số thập phân
    }

    @Override
    public List<ReviewDetail> getUserReviews(Long userId) {
        // Kiểm tra user tồn tại
        if (!accountRepository.existsById(userId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại");
        }

        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(review -> convertToReviewDetail(review, review.getUser().getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserReviewedCompany(Long userId, Long companyId) {
        return reviewRepository.existsByUserIdAndCompanyId(userId, companyId);
    }

    /**
     * Helper method để convert Review entity thành ReviewDetail DTO
     */
    private ReviewDetail convertToReviewDetail(Review review, String userName) {
        return new ReviewDetail(
                review.getId(),
                userName,
                review.getRating(),
                review.getComment(),
                review.getIsVerified(),
                review.getCreatedAt()
        );
    }
}
