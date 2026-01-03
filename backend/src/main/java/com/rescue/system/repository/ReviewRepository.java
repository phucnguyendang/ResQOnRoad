package com.rescue.system.repository;

import com.rescue.system.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    Page<Review> findByCompanyId(Long companyId, Pageable pageable);

    List<Review> findByUserId(Long userId);

    List<Review> findByUserIdAndCompanyId(Long userId, Long companyId);

    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);
}
