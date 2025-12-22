package com.rescue.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rescue_status_history")
public class RescueStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rescue_request_id", nullable = false)
    private RescueRequest rescueRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 50)
    private RescueStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 50)
    private RescueStatus newStatus;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt = Instant.now();

    // Constructors
    public RescueStatusHistory() {
    }

    public RescueStatusHistory(RescueRequest rescueRequest, RescueStatus previousStatus, RescueStatus newStatus, String changedBy) {
        this.rescueRequest = rescueRequest;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueRequest getRescueRequest() {
        return rescueRequest;
    }

    public void setRescueRequest(RescueRequest rescueRequest) {
        this.rescueRequest = rescueRequest;
    }

    public RescueStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(RescueStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public RescueStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RescueStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }
}
