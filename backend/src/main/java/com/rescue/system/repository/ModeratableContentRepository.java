package com.rescue.system.repository;

import com.rescue.system.entity.ContentStatus;
import com.rescue.system.entity.ContentType;
import com.rescue.system.entity.ModeratableContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ModeratableContent entity
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 */
@Repository
public interface ModeratableContentRepository extends JpaRepository<ModeratableContent, Long> {

    /**
     * Find all contents with a specific status
     */
    Page<ModeratableContent> findByStatusOrderByCreatedAtDesc(ContentStatus status, Pageable pageable);

    /**
     * Find all contents with a specific status (list)
     */
    List<ModeratableContent> findByStatusOrderByCreatedAtDesc(ContentStatus status);

    /**
     * Find all pending contents
     */
    Page<ModeratableContent> findByStatusOrderByCreatedAtAsc(ContentStatus status, Pageable pageable);

    /**
     * Find contents by type and status
     */
    Page<ModeratableContent> findByContentTypeAndStatusOrderByCreatedAtDesc(
            ContentType contentType, ContentStatus status, Pageable pageable);

    /**
     * Find contents by author
     */
    Page<ModeratableContent> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * Find content by type and reference ID
     */
    Optional<ModeratableContent> findByContentTypeAndReferenceId(ContentType contentType, Long referenceId);

    /**
     * Check if content already exists for moderation
     */
    boolean existsByContentTypeAndReferenceId(ContentType contentType, Long referenceId);

    /**
     * Count contents by status
     */
    long countByStatus(ContentStatus status);

    /**
     * Count contents by type
     */
    long countByContentType(ContentType contentType);

    /**
     * Count contents by type and status
     */
    long countByContentTypeAndStatus(ContentType contentType, ContentStatus status);

    /**
     * Find all contents ordered by creation date (newest first)
     */
    Page<ModeratableContent> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Search contents by text
     */
    @Query("SELECT mc FROM ModeratableContent mc WHERE " +
            "(LOWER(mc.contentTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(mc.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY mc.createdAt DESC")
    Page<ModeratableContent> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Search contents by text and status
     */
    @Query("SELECT mc FROM ModeratableContent mc WHERE " +
            "mc.status = :status AND " +
            "(LOWER(mc.contentTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(mc.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY mc.createdAt DESC")
    Page<ModeratableContent> searchByKeywordAndStatus(
            @Param("keyword") String keyword,
            @Param("status") ContentStatus status,
            Pageable pageable);

    /**
     * Find contents by multiple statuses
     */
    @Query("SELECT mc FROM ModeratableContent mc WHERE mc.status IN :statuses ORDER BY mc.createdAt DESC")
    Page<ModeratableContent> findByStatusIn(@Param("statuses") List<ContentStatus> statuses, Pageable pageable);

    /**
     * Delete by content type and reference ID
     */
    void deleteByContentTypeAndReferenceId(ContentType contentType, Long referenceId);
}
