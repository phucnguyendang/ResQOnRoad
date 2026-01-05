package com.rescue.system.repository;

import com.rescue.system.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository cho thao tác với bình luận/tư vấn trong cộng đồng
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * Lấy danh sách bình luận của một bài đăng
     */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    
    /**
     * Lấy danh sách bình luận của một bài đăng với phân trang
     */
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);
}
