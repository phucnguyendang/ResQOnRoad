package com.rescue.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho một bài đăng trong cộng đồng
 * Người dùng có thể tạo bài đăng để yêu cầu tư vấn hoặc chia sẻ kinh nghiệm
 */
@Entity
@Table(name = "community_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = PostStatus.OPEN;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Lấy số lượng bình luận
     */
    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }
}
