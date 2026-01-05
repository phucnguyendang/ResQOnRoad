package com.rescue.system.service;

import com.rescue.system.dto.request.CreateCommunityCommentRequest;
import com.rescue.system.dto.request.CreateCommunityPostRequest;
import com.rescue.system.dto.request.UpdateCommunityPostRequest;
import com.rescue.system.dto.response.CommunityCommentDto;
import com.rescue.system.dto.response.CommunityPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Community Support features
 * Part of UC103: Community Support and Consultation
 */
public interface CommunityService {

    // ==================== POST OPERATIONS ====================

    /**
     * Create a new community post
     * UC103 - Step 1: User creates a post about their incident
     */
    CommunityPostDto createPost(Long authorId, CreateCommunityPostRequest request);

    /**
     * Get post by ID
     */
    CommunityPostDto getPostById(Long postId);

    /**
     * Get post by ID with incremented view count
     * UC103 - Step 2: System displays the post to other users
     */
    CommunityPostDto getPostByIdAndIncrementView(Long postId);

    /**
     * Get all posts (paginated)
     * UC103 - Step 2: Display posts for community feed
     */
    Page<CommunityPostDto> getAllPosts(Pageable pageable);

    /**
     * Get all posts by author
     */
    Page<CommunityPostDto> getPostsByAuthor(Long authorId, Pageable pageable);

    /**
     * Search posts by keyword
     */
    Page<CommunityPostDto> searchPosts(String keyword, Pageable pageable);

    /**
     * Get posts by incident type
     */
    List<CommunityPostDto> getPostsByIncidentType(String incidentType);

    /**
     * Get unresolved posts (need help)
     */
    List<CommunityPostDto> getUnresolvedPosts();

    /**
     * Get nearby posts by location
     */
    List<CommunityPostDto> getNearbyPosts(Double latitude, Double longitude, Double radiusKm);

    /**
     * Get most viewed/popular posts
     */
    List<CommunityPostDto> getPopularPosts();

    /**
     * Update a post
     */
    CommunityPostDto updatePost(Long postId, Long authorId, UpdateCommunityPostRequest request);

    /**
     * Mark post as resolved
     * UC103 - Step 5: User marks the issue as resolved after receiving helpful
     * advice
     */
    CommunityPostDto markPostAsResolved(Long postId, Long authorId);

    /**
     * Delete a post
     */
    void deletePost(Long postId, Long authorId);

    // ==================== COMMENT OPERATIONS ====================

    /**
     * Add a comment to a post
     * UC103 - Step 3: Community provides advice/solutions
     */
    CommunityCommentDto addComment(Long postId, Long authorId, CreateCommunityCommentRequest request);

    /**
     * Get all comments for a post
     * UC103 - Step 4: System displays community advice
     */
    List<CommunityCommentDto> getCommentsByPostId(Long postId);

    /**
     * Get comments for a post (paginated)
     */
    Page<CommunityCommentDto> getCommentsByPostId(Long postId, Pageable pageable);

    /**
     * Update a comment
     */
    CommunityCommentDto updateComment(Long commentId, Long authorId, String newContent);

    /**
     * Mark comment as helpful
     */
    CommunityCommentDto markCommentAsHelpful(Long commentId, Long postAuthorId);

    /**
     * Increment helpful count for a comment (like/upvote)
     */
    CommunityCommentDto incrementHelpfulCount(Long commentId);

    /**
     * Delete a comment
     */
    void deleteComment(Long commentId, Long authorId);

    /**
     * Get helpful comments for a post
     */
    List<CommunityCommentDto> getHelpfulComments(Long postId);

    // ==================== STATISTICS ====================

    /**
     * Get post count for a user
     */
    long getPostCountByAuthor(Long authorId);

    /**
     * Get comment count for a user
     */
    long getCommentCountByAuthor(Long authorId);
}
