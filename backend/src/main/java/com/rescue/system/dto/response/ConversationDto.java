package com.rescue.system.dto.response;

import com.rescue.system.entity.ConversationStatus;
import java.time.Instant;
import java.util.List;

/**
 * DTO for conversation response with messages
 */
public class ConversationDto {

    private Long id;
    private Long requestId;
    private Long userId;
    private String userName;
    private Long companyId;
    private String companyName;
    private Instant startedAt;
    private Instant endedAt;
    private ConversationStatus status;
    private Double agreedCost;
    private String costNotes;
    private List<MessageDto> messages;
    private int unreadCount;

    // Constructors
    public ConversationDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }

    public Double getAgreedCost() {
        return agreedCost;
    }

    public void setAgreedCost(Double agreedCost) {
        this.agreedCost = agreedCost;
    }

    public String getCostNotes() {
        return costNotes;
    }

    public void setCostNotes(String costNotes) {
        this.costNotes = costNotes;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
