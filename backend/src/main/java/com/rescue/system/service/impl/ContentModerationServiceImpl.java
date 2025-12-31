package com.rescue.system.service.impl;

import com.rescue.system.dto.request.ModerationActionRequest;
import com.rescue.system.dto.response.ModeratableContentResponse;
import com.rescue.system.dto.response.ModerationListResponse;
import com.rescue.system.entity.*;
import com.rescue.system.exception.ResourceNotFoundException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.ModeratableContentRepository;
import com.rescue.system.service.ContentModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ContentModerationService
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 */
@Service
public class ContentModerationServiceImpl implements ContentModerationService {

    private static final Logger logger = LoggerFactory.getLogger(ContentModerationServiceImpl.class);

    private final ModeratableContentRepository contentRepository;
    private final AccountRepository accountRepository;

    public ContentModerationServiceImpl(ModeratableContentRepository contentRepository,
            AccountRepository accountRepository) {
        this.contentRepository = contentRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public ModerationListResponse getPendingContent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ModeratableContent> contentPage = contentRepository
                .findByStatusOrderByCreatedAtAsc(ContentStatus.PENDING, pageable);

        return buildListResponse(contentPage);
    }

    @Override
    public ModerationListResponse getContentList(ContentStatus status, ContentType contentType,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ModeratableContent> contentPage;

        if (status != null && contentType != null) {
            contentPage = contentRepository.findByContentTypeAndStatusOrderByCreatedAtDesc(
                    contentType, status, pageable);
        } else if (status != null) {
            contentPage = contentRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            contentPage = contentRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return buildListResponse(contentPage);
    }

    @Override
    public ModeratableContentResponse getContentDetail(Long contentId) {
        ModeratableContent content = findContentById(contentId);
        return ModeratableContentResponse.fromEntity(content);
    }

    @Override
    @Transactional
    public ModeratableContentResponse approveContent(Long contentId, String moderatorUsername) {
        ModeratableContent content = findContentById(contentId);
        Account moderator = findAccountByUsername(moderatorUsername);

        validatePendingStatus(content);

        content.setStatus(ContentStatus.APPROVED);
        content.setModerator(moderator);
        content.setModeratedAt(LocalDateTime.now());

        ModeratableContent savedContent = contentRepository.save(content);
        logger.info("Content {} approved by admin {}", contentId, moderatorUsername);

        return ModeratableContentResponse.fromEntity(savedContent);
    }

    @Override
    @Transactional
    public ModeratableContentResponse rejectContent(Long contentId, String reason, String moderatorUsername) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required for rejecting content");
        }

        ModeratableContent content = findContentById(contentId);
        Account moderator = findAccountByUsername(moderatorUsername);

        validatePendingStatus(content);

        content.setStatus(ContentStatus.REJECTED);
        content.setModerator(moderator);
        content.setModerationReason(reason);
        content.setModeratedAt(LocalDateTime.now());

        ModeratableContent savedContent = contentRepository.save(content);
        logger.info("Content {} rejected by admin {} with reason: {}", contentId, moderatorUsername, reason);

        return ModeratableContentResponse.fromEntity(savedContent);
    }

    @Override
    @Transactional
    public ModeratableContentResponse removeContent(Long contentId, String reason, String moderatorUsername) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required for removing content");
        }

        ModeratableContent content = findContentById(contentId);
        Account moderator = findAccountByUsername(moderatorUsername);

        // Can remove both pending and approved content
        if (content.getStatus() == ContentStatus.REMOVED) {
            throw new IllegalStateException("Content is already removed");
        }

        content.setStatus(ContentStatus.REMOVED);
        content.setModerator(moderator);
        content.setModerationReason(reason);
        content.setModeratedAt(LocalDateTime.now());

        ModeratableContent savedContent = contentRepository.save(content);
        logger.info("Content {} removed by admin {} with reason: {}", contentId, moderatorUsername, reason);

        return ModeratableContentResponse.fromEntity(savedContent);
    }

    @Override
    @Transactional
    public ModeratableContentResponse processModeration(Long contentId, ModerationActionRequest request,
            String moderatorUsername) {
        String action = request.getAction().toUpperCase();

        return switch (action) {
            case "APPROVE" -> approveContent(contentId, moderatorUsername);
            case "REJECT" -> rejectContent(contentId, request.getReason(), moderatorUsername);
            case "REMOVE" -> removeContent(contentId, request.getReason(), moderatorUsername);
            default -> throw new IllegalArgumentException("Invalid action: " + action +
                    ". Valid actions are: APPROVE, REJECT, REMOVE");
        };
    }

    @Override
    @Transactional
    public void deleteContent(Long contentId, String moderatorUsername) {
        ModeratableContent content = findContentById(contentId);

        logger.info("Content {} deleted permanently by admin {}", contentId, moderatorUsername);
        contentRepository.delete(content);
    }

    @Override
    @Transactional
    public ModeratableContent submitForModeration(ModeratableContent content) {
        // Check if already submitted
        if (contentRepository.existsByContentTypeAndReferenceId(
                content.getContentType(), content.getReferenceId())) {
            throw new IllegalStateException("Content is already submitted for moderation");
        }

        content.setStatus(ContentStatus.PENDING);
        ModeratableContent savedContent = contentRepository.save(content);

        logger.info("New content submitted for moderation: type={}, refId={}",
                content.getContentType(), content.getReferenceId());

        return savedContent;
    }

    @Override
    public ModerationListResponse searchContent(String keyword, ContentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ModeratableContent> contentPage;

        if (status != null) {
            contentPage = contentRepository.searchByKeywordAndStatus(keyword, status, pageable);
        } else {
            contentPage = contentRepository.searchByKeyword(keyword, pageable);
        }

        return buildListResponse(contentPage);
    }

    @Override
    public ModerationListResponse getStatistics() {
        ModerationListResponse response = new ModerationListResponse();

        response.setPendingCount(contentRepository.countByStatus(ContentStatus.PENDING));
        response.setApprovedCount(contentRepository.countByStatus(ContentStatus.APPROVED));
        response.setRejectedCount(contentRepository.countByStatus(ContentStatus.REJECTED));
        response.setRemovedCount(contentRepository.countByStatus(ContentStatus.REMOVED));
        response.setTotalElements(contentRepository.count());

        return response;
    }

    // ==================== Helper Methods ====================

    private ModeratableContent findContentById(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + contentId));
    }

    private Account findAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + username));
    }

    private void validatePendingStatus(ModeratableContent content) {
        if (content.getStatus() != ContentStatus.PENDING) {
            throw new IllegalStateException("Content is not pending. Current status: " + content.getStatus());
        }
    }

    private ModerationListResponse buildListResponse(Page<ModeratableContent> contentPage) {
        ModerationListResponse response = new ModerationListResponse();

        List<ModeratableContentResponse> contentList = contentPage.getContent().stream()
                .map(ModeratableContentResponse::fromEntity)
                .collect(Collectors.toList());

        response.setContents(contentList);
        response.setCurrentPage(contentPage.getNumber());
        response.setTotalPages(contentPage.getTotalPages());
        response.setTotalElements(contentPage.getTotalElements());
        response.setPageSize(contentPage.getSize());
        response.setHasNext(contentPage.hasNext());
        response.setHasPrevious(contentPage.hasPrevious());

        // Add statistics
        response.setPendingCount(contentRepository.countByStatus(ContentStatus.PENDING));
        response.setApprovedCount(contentRepository.countByStatus(ContentStatus.APPROVED));
        response.setRejectedCount(contentRepository.countByStatus(ContentStatus.REJECTED));
        response.setRemovedCount(contentRepository.countByStatus(ContentStatus.REMOVED));

        return response;
    }
}
