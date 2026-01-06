package com.rescue.system.controller;

import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.CommunityCommentDto;
import com.rescue.system.dto.response.CommunityPostDto;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.CommunityPost;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.CommunityCommentRepository;
import com.rescue.system.repository.CommunityPostRepository;
import com.rescue.system.repository.AccountRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/community-posts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommunityPostsController {

    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final AccountRepository accountRepository;

    public AdminCommunityPostsController(
            CommunityPostRepository postRepository,
            CommunityCommentRepository commentRepository,
            AccountRepository accountRepository
    ) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/deleted")
    public ApiResponse<List<CommunityPostDto>> listDeletedPosts() {
        List<CommunityPostDto> items = postRepository.findByIsDeletedTrueOrderByDeletedAtDesc()
                .stream()
                .map(p -> mapToPostDto(p, false))
                .collect(Collectors.toList());
        return ApiResponse.of("Lấy danh sách bài viết đã xóa thành công", items);
    }

    @GetMapping("/closed")
    public ApiResponse<List<CommunityPostDto>> listClosedCommentPosts() {
        List<CommunityPostDto> items = postRepository.findByIsResolvedTrueAndIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(p -> mapToPostDto(p, false))
                .collect(Collectors.toList());
        return ApiResponse.of("Lấy danh sách bài viết đã đóng bình luận thành công", items);
    }

    @GetMapping("/{id}")
    public ApiResponse<CommunityPostDto> getPostDetail(@PathVariable Long id) {
        CommunityPost post = postRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));
        return ApiResponse.of("Lấy chi tiết bài đăng thành công", mapToPostDto(post, true));
    }

    @PatchMapping("/{id}/restore")
    public ApiResponse<CommunityPostDto> restorePost(@PathVariable Long id) {
        CommunityPost post = postRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));

        if (!Boolean.TRUE.equals(post.getIsDeleted())) {
            return ApiResponse.of("Bài đăng không ở trạng thái đã xóa", mapToPostDto(post, true));
        }

        post.setIsDeleted(false);
        post.setDeletedAt(null);
        post.setUpdatedAt(Instant.now());
        CommunityPost saved = postRepository.save(post);
        return ApiResponse.of("Khôi phục bài đăng thành công", mapToPostDto(saved, true));
    }

    @PatchMapping("/{id}/comments/open")
    public ApiResponse<CommunityPostDto> openComments(@PathVariable Long id) {
        CommunityPost post = postRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng"));

        post.setIsResolved(false);
        post.setUpdatedAt(Instant.now());
        CommunityPost saved = postRepository.save(post);
        return ApiResponse.of("Đã mở bình luận", mapToPostDto(saved, true));
    }

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

    private CommunityCommentDto mapToCommentDto(com.rescue.system.entity.CommunityComment comment) {
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
        Account hydrated = account;
        if (hydrated != null && hydrated.getId() != null) {
            hydrated = accountRepository.findById(hydrated.getId()).orElse(hydrated);
        }
        return new CommunityPostDto.AuthorInfo(
                hydrated.getId(),
                hydrated.getUsername(),
                hydrated.getFullName(),
                hydrated.getAvatarBase64());
    }
}
