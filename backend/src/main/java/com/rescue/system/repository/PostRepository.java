package com.rescue.system.repository;

import com.rescue.system.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository cho thao tác với bài đăng cộng đồng
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    /**
     * Lấy danh sách bài đăng với phân trang
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Lấy bài đăng theo ID
     */
    Optional<Post> findById(Long id);
}
