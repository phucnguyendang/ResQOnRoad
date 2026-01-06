package com.rescue.system.service;

import com.rescue.system.dto.request.ModerationActionRequest;
import com.rescue.system.dto.response.ModeratableContentResponse;
import com.rescue.system.dto.response.ModerationListResponse;
import com.rescue.system.entity.ContentStatus;
import com.rescue.system.entity.ContentType;
import com.rescue.system.entity.ModeratableContent;
import org.springframework.data.domain.Page;

/**
 * Service interface for content moderation
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 */
public interface ContentModerationService {

    /**
     * Get list of all content pending moderation
     * (Lấy danh sách nội dung chờ duyệt)
     * 
     * @param page page number (0-indexed)
     * @param size page size
     * @return paginated list of pending content
     */
    ModerationListResponse getPendingContent(int page, int size);

    /**
     * Get list of all content with filters
     * (Lấy danh sách nội dung với bộ lọc)
     * 
     * @param status      filter by status (null for all)
     * @param contentType filter by content type (null for all)
     * @param page        page number
     * @param size        page size
     * @return paginated list of content
     */
    ModerationListResponse getContentList(ContentStatus status, ContentType contentType, int page, int size);

    /**
     * Get detailed information of a specific content
     * (Xem chi tiết một nội dung)
     * 
     * @param contentId the content ID
     * @return content details
     */
    ModeratableContentResponse getContentDetail(Long contentId);

    /**
     * Approve content
     * (Phê duyệt nội dung)
     * 
     * @param contentId         the content ID
     * @param moderatorUsername username of the admin performing the action
     * @return updated content
     */
    ModeratableContentResponse approveContent(Long contentId, String moderatorUsername);

    /**
     * Reject content
     * (Từ chối nội dung)
     * 
     * @param contentId         the content ID
     * @param reason            the reason for rejection
     * @param moderatorUsername username of the admin performing the action
     * @return updated content
     */
    ModeratableContentResponse rejectContent(Long contentId, String reason, String moderatorUsername);

    /**
     * Remove content (for already approved content that violates rules)
     * (Gỡ bỏ nội dung)
     * 
     * @param contentId         the content ID
     * @param reason            the reason for removal
     * @param moderatorUsername username of the admin performing the action
     * @return updated content
     */
    ModeratableContentResponse removeContent(Long contentId, String reason, String moderatorUsername);

    /**
     * Process moderation action (approve, reject, or remove)
     * (Xử lý kiểm duyệt)
     * 
     * @param contentId         the content ID
     * @param request           the moderation action request
     * @param moderatorUsername username of the admin performing the action
     * @return updated content
     */
    ModeratableContentResponse processModeration(Long contentId, ModerationActionRequest request,
            String moderatorUsername);

    /**
     * Delete content permanently (for severe violations)
     * (Xóa nội dung vĩnh viễn)
     * 
     * @param contentId         the content ID
     * @param moderatorUsername username of the admin performing the action
     */
    void deleteContent(Long contentId, String moderatorUsername);

    /**
     * Submit content for moderation
     * (Gửi nội dung để kiểm duyệt)
     * 
     * @param content the content to submit
     * @return created moderation entry
     */
    ModeratableContent submitForModeration(ModeratableContent content);

    /**
     * Search content by keyword
     * (Tìm kiếm nội dung theo từ khóa)
     * 
     * @param keyword search keyword
     * @param status  filter by status (null for all)
     * @param page    page number
     * @param size    page size
     * @return paginated search results
     */
    ModerationListResponse searchContent(String keyword, ContentStatus status, int page, int size);

    /**
     * Get moderation statistics
     * (Lấy thống kê kiểm duyệt)
     * 
     * @return statistics as a response object
     */
    ModerationListResponse getStatistics();
}
