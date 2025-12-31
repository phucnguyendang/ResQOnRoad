package com.rescue.system.repository;

import com.rescue.system.entity.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for CommunityComment entity
 * Part of UC103: Community Support and Consultation
 */
@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    /**
     * Find all comments for a post ordered by creation time
     */
    List<CommunityComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    /**
     * Find all comments for a post with pagination
     */
    Page<CommunityComment> findByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);

    /**
     * Find top-level comments (no parent) for a post
     */
    List<CommunityComment> findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(Long postId);

    /**
     * Find replies to a specific comment
     */
    List<CommunityComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);

    /**
     * Find all comments by author ID
     */
    List<CommunityComment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    /**
     * Find helpful comments for a post
     */
    List<CommunityComment> findByPostIdAndIsHelpfulTrueOrderByHelpfulCountDesc(Long postId);

    /**
     * Count comments for a post
     */
    long countByPostId(Long postId);

    /**
     * Count comments by author
     */
    long countByAuthorId(Long authorId);

    /**
     * Delete all comments for a post
     */
    void deleteByPostId(Long postId);
}
