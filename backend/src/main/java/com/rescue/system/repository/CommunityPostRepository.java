package com.rescue.system.repository;

import com.rescue.system.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for CommunityPost entity
 * Part of UC103: Community Support and Consultation
 */
@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

       /**
        * Find all posts ordered by creation time (newest first)
        */
       List<CommunityPost> findAllByOrderByCreatedAtDesc();

       /**
        * Find all posts with pagination
        */
       Page<CommunityPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

       /**
        * Find posts by author ID
        */
       List<CommunityPost> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

       /**
        * Find posts by author ID with pagination
        */
       Page<CommunityPost> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

       /**
        * Find posts by incident type
        */
       List<CommunityPost> findByIncidentTypeOrderByCreatedAtDesc(String incidentType);

       /**
        * Find unresolved posts
        */
       List<CommunityPost> findByIsResolvedFalseOrderByCreatedAtDesc();

       /**
        * Find resolved posts
        */
       List<CommunityPost> findByIsResolvedTrueOrderByCreatedAtDesc();

       /**
        * Search posts by title or content
        */
       @Query("SELECT p FROM CommunityPost p WHERE " +
                     "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                     "ORDER BY p.createdAt DESC")
       List<CommunityPost> searchByKeyword(@Param("keyword") String keyword);

       /**
        * Search posts by title or content with pagination
        */
       @Query("SELECT p FROM CommunityPost p WHERE " +
                     "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                     "ORDER BY p.createdAt DESC")
       Page<CommunityPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

       /**
        * Find posts nearby a location
        */
       @Query("SELECT p FROM CommunityPost p WHERE " +
                     "p.latitude IS NOT NULL AND p.longitude IS NOT NULL AND " +
                     "p.latitude BETWEEN :minLat AND :maxLat AND " +
                     "p.longitude BETWEEN :minLng AND :maxLng " +
                     "ORDER BY p.createdAt DESC")
       List<CommunityPost> findNearbyPosts(
                     @Param("minLat") Double minLat,
                     @Param("maxLat") Double maxLat,
                     @Param("minLng") Double minLng,
                     @Param("maxLng") Double maxLng);

       /**
        * Count posts by author ID
        */
       long countByAuthorId(Long authorId);

       /**
        * Find most viewed posts
        */
       List<CommunityPost> findTop10ByOrderByViewCountDesc();
}
