package com.rescue.system.dto.response;

import java.util.List;

/**
 * Response DTO for paginated moderation content list
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 */
public class ModerationListResponse {

    private List<ModeratableContentResponse> contents;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    // Statistics
    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;
    private long removedCount;

    public ModerationListResponse() {
    }

    // Getters and Setters
    public List<ModeratableContentResponse> getContents() {
        return contents;
    }

    public void setContents(List<ModeratableContentResponse> contents) {
        this.contents = contents;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(long approvedCount) {
        this.approvedCount = approvedCount;
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public long getRemovedCount() {
        return removedCount;
    }

    public void setRemovedCount(long removedCount) {
        this.removedCount = removedCount;
    }
}
