package com.rescue.system.service.impl;

import com.rescue.system.dto.request.CreateCommunityCommentRequest;
import com.rescue.system.dto.request.CreateCommunityPostRequest;
import com.rescue.system.dto.request.UpdateCommunityPostRequest;
import com.rescue.system.dto.response.CommunityCommentDto;
import com.rescue.system.dto.response.CommunityPostDto;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.CommunityComment;
import com.rescue.system.entity.CommunityPost;
import com.rescue.system.entity.ContentStatus;
import com.rescue.system.entity.ContentType;
import com.rescue.system.entity.ModeratableContent;
import com.rescue.system.entity.Role;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.CommunityCommentRepository;
import com.rescue.system.repository.CommunityPostRepository;
import com.rescue.system.repository.ModeratableContentRepository;
import com.rescue.system.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CommunityService
 * Part of UC103: Community Support and Consultation
 */
@Service
@Transactional
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private CommunityCommentRepository commentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModeratableContentRepository moderatableContentRepository;

    // ==================== POST OPERATIONS ====================

    private Account requireAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    private boolean isAdmin(Account account) {
        return account != null && account.getRole() == Role.ADMIN;
    }

    @Override
    public CommunityPostDto createPost(Long authorId, CreateCommunityPostRequest request) {
        Account author = accountRepository.findById(authorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        CommunityPost post = new CommunityPost();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);
        post.setIncidentType(request.getIncidentType());
        post.setLocation(request.getLocation());
        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());
        post.setImageBase64(request.getImageBase64());
        post.setCreatedAt(Instant.now());

        CommunityPost savedPost = postRepository.save(post);

        // Create moderation content for UC405 - Content Moderation
        ModeratableContent moderatableContent = new ModeratableContent(
                ContentType.POST,
                savedPost.getId(),
                author,
                savedPost.getContent(),
                savedPost.getTitle(),
                savedPost.getImageBase64());
        moderatableContent.setStatus(ContentStatus.PENDING);
        moderatableContentRepository.save(moderatableContent);

        return mapToPostDto(savedPost, false);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityPostDto getPostById(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));
        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng");
        }
        return mapToPostDto(post, true);
    }

    @Override
    public CommunityPostDto getPostByIdAndIncrementView(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));
        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng");
        }
        post.incrementViewCount();
        postRepository.save(post);
        return mapToPostDto(post, true);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommunityPostDto> getAllPosts(Pageable pageable) {
        return postRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(post -> mapToPostDto(post, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommunityPostDto> getPostsByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(authorId, pageable)
                .map(post -> mapToPostDto(post, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommunityPostDto> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable)
                .map(post -> mapToPostDto(post, false));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostDto> getPostsByIncidentType(String incidentType) {
        return postRepository.findByIncidentTypeOrderByCreatedAtDesc(incidentType)
                .stream()
                .map(post -> mapToPostDto(post, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostDto> getUnresolvedPosts() {
        return postRepository.findByIsResolvedFalseAndIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(post -> mapToPostDto(post, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostDto> getNearbyPosts(Double latitude, Double longitude, Double radiusKm) {
        // Approximate conversion: 1 degree ≈ 111 km
        double latDelta = radiusKm / 111.0;
        double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));

        return postRepository.findNearbyPosts(
                latitude - latDelta,
                latitude + latDelta,
                longitude - lngDelta,
                longitude + lngDelta)
                .stream()
                .map(post -> mapToPostDto(post, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostDto> getPopularPosts() {
        return postRepository.findTop10ByOrderByViewCountDesc()
                .stream()
                .map(post -> mapToPostDto(post, false))
                .collect(Collectors.toList());
    }

    @Override
    public CommunityPostDto updatePost(Long postId, Long authorId, UpdateCommunityPostRequest request) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));

        if (!post.getAuthor().getId().equals(authorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền chỉnh sửa bài đăng này");
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getIncidentType() != null) {
            post.setIncidentType(request.getIncidentType());
        }
        if (request.getLocation() != null) {
            post.setLocation(request.getLocation());
        }
        if (request.getLatitude() != null) {
            post.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            post.setLongitude(request.getLongitude());
        }
        if (request.getImageBase64() != null) {
            post.setImageBase64(request.getImageBase64());
        }
        if (request.getIsResolved() != null) {
            post.setIsResolved(request.getIsResolved());
        }
        post.setUpdatedAt(Instant.now());

        CommunityPost updatedPost = postRepository.save(post);
        return mapToPostDto(updatedPost, true);
    }

    @Override
    public CommunityPostDto markPostAsResolved(Long postId, Long authorId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));

        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng");
        }

        Account actor = requireAccount(authorId);

        if (!isAdmin(actor) && !post.getAuthor().getId().equals(authorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Chỉ tác giả mới có thể đánh dấu bài đăng đã giải quyết");
        }

        post.setIsResolved(true);
        post.setUpdatedAt(Instant.now());

        CommunityPost updatedPost = postRepository.save(post);
        return mapToPostDto(updatedPost, true);
    }

    @Override
    public CommunityPostDto setPostResolved(Long postId, Long actorId, boolean resolved) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));

        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng");
        }

        Account actor = requireAccount(actorId);
        if (!isAdmin(actor) && !post.getAuthor().getId().equals(actorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền thay đổi trạng thái bình luận của bài đăng này");
        }

        post.setIsResolved(resolved);
        post.setUpdatedAt(Instant.now());
        CommunityPost updated = postRepository.save(post);
        return mapToPostDto(updated, true);
    }

    @Override
    public void deletePost(Long postId, Long authorId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));

        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng");
        }

        Account actor = requireAccount(authorId);

        if (!isAdmin(actor) && !post.getAuthor().getId().equals(authorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa bài đăng này");
        }

        post.setIsDeleted(true);
        post.setDeletedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postRepository.save(post);
    }

    // ==================== COMMENT OPERATIONS ====================

    @Override
    public CommunityCommentDto addComment(Long postId, Long authorId, CreateCommunityCommentRequest request) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));

        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng");
        }

        if (Boolean.TRUE.equals(post.getIsResolved())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bài đăng đã đóng bình luận");
        }

        Account author = requireAccount(authorId);

        CommunityComment comment = new CommunityComment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setCreatedAt(Instant.now());

        // Handle reply to another comment
        if (request.getParentCommentId() != null) {
            CommunityComment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận gốc"));
            comment.setParentComment(parentComment);
        }

        CommunityComment savedComment = commentRepository.save(comment);

        // Create moderation content for UC405 - Content Moderation
        ModeratableContent moderatableContent = new ModeratableContent(
                ContentType.COMMENT,
                savedComment.getId(),
                author,
                savedComment.getContent(),
                null, // Comments don't have titles
                null // Comments don't have images
        );
        moderatableContent.setStatus(ContentStatus.PENDING);
        moderatableContentRepository.save(moderatableContent);

        return mapToCommentDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityCommentDto> getCommentsByPostId(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));
        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng");
        }

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommunityCommentDto> getCommentsByPostId(Long postId, Pageable pageable) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));
        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng");
        }

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable)
                .map(this::mapToCommentDto);
    }

    @Override
    public CommunityCommentDto updateComment(Long commentId, Long authorId, String newContent) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận"));

        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền chỉnh sửa bình luận này");
        }

        comment.setContent(newContent);
        comment.setUpdatedAt(Instant.now());

        CommunityComment updatedComment = commentRepository.save(comment);
        return mapToCommentDto(updatedComment);
    }

    @Override
    public CommunityCommentDto markCommentAsHelpful(Long commentId, Long postAuthorId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận"));

        // Only the post author can mark a comment as helpful
        if (!comment.getPost().getAuthor().getId().equals(postAuthorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Chỉ tác giả bài đăng mới có thể đánh dấu bình luận hữu ích");
        }

        comment.setIsHelpful(true);
        comment.setUpdatedAt(Instant.now());

        CommunityComment updatedComment = commentRepository.save(comment);
        return mapToCommentDto(updatedComment);
    }

    @Override
    public CommunityCommentDto incrementHelpfulCount(Long commentId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận"));

        comment.incrementHelpfulCount();

        CommunityComment updatedComment = commentRepository.save(comment);
        return mapToCommentDto(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId, Long authorId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận"));

        Account actor = requireAccount(authorId);

        // USER/COMPANY: only own comment. ADMIN: any comment.
        if (!isAdmin(actor) && !comment.getAuthor().getId().equals(authorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa bình luận này");
        }

        commentRepository.delete(comment);
    }

    @Override
    public CommunityCommentDto closeComment(Long commentId, Long actorId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận"));

        Account actor = requireAccount(actorId);

        // USER/COMPANY: only own comment. ADMIN: any comment.
        if (!isAdmin(actor) && !comment.getAuthor().getId().equals(actorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền đóng bình luận này");
        }

        comment.setIsClosed(true);
        comment.setUpdatedAt(Instant.now());
        CommunityComment updated = commentRepository.save(comment);
        return mapToCommentDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityCommentDto> getHelpfulComments(Long postId) {
        return commentRepository.findByPostIdAndIsHelpfulTrueOrderByHelpfulCountDesc(postId)
                .stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());
    }

    // ==================== STATISTICS ====================

    @Override
    @Transactional(readOnly = true)
    public long getPostCountByAuthor(Long authorId) {
        return postRepository.countByAuthorId(authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByAuthor(Long authorId) {
        return commentRepository.countByAuthorId(authorId);
    }

    // ==================== MAPPING METHODS ====================

    private CommunityPostDto mapToPostDto(CommunityPost post, boolean includeComments) {
        CommunityPostDto dto = new CommunityPostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(mapToAuthorInfo(post.getAuthor()));
        dto.setIncidentType(post.getIncidentType());
        dto.setLocation(post.getLocation());
        dto.setLatitude(post.getLatitude());
        dto.setLongitude(post.getLongitude());
        dto.setImageBase64(post.getImageBase64());
        dto.setViewCount(post.getViewCount());
        dto.setIsResolved(post.getIsResolved());
        dto.setIsDeleted(post.getIsDeleted());
        dto.setDeletedAt(post.getDeletedAt());
        dto.setCommentCount((int) commentRepository.countByPostId(post.getId()));
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        if (includeComments) {
            List<CommunityCommentDto> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId())
                    .stream()
                    .map(this::mapToCommentDto)
                    .collect(Collectors.toList());
            dto.setComments(comments);
        }

        return dto;
    }

    private CommunityCommentDto mapToCommentDto(CommunityComment comment) {
        CommunityCommentDto dto = new CommunityCommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthor(mapToAuthorInfo(comment.getAuthor()));
        dto.setPostId(comment.getPost().getId());
        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        dto.setIsHelpful(comment.getIsHelpful());
        dto.setHelpfulCount(comment.getHelpfulCount());
        dto.setIsClosed(comment.getIsClosed());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }

    private CommunityPostDto.AuthorInfo mapToAuthorInfo(Account account) {
        return new CommunityPostDto.AuthorInfo(
                account.getId(),
                account.getUsername(),
                account.getFullName(),
                account.getAvatarBase64());
    }
}
