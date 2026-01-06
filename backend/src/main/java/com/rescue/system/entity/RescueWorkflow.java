package com.rescue.system.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity đại diện cho quy trình cứu hộ (QuyTrinhCuuHo)
 * Mô tả chi tiết từng bước xử lý khi nhận được yêu cầu cứu hộ
 */
@Entity
@Table(name = "rescue_workflows", indexes = {
        @Index(name = "idx_workflow_request_id", columnList = "rescue_request_id"),
        @Index(name = "idx_workflow_status", columnList = "workflow_status"),
        @Index(name = "idx_workflow_company_id", columnList = "company_id")
})
public class RescueWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "rescue_request_id", nullable = false, unique = true)
    private RescueRequest rescueRequest;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Account company;

    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_status", nullable = false, length = 50)
    private WorkflowStatus workflowStatus = WorkflowStatus.PENDING;

    @Column(name = "processing_steps", columnDefinition = "TEXT")
    private String processingSteps;

    @Column(name = "current_step", length = 100)
    private String currentStep;

    @Column(name = "step_number")
    private Integer stepNumber = 0;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "estimated_arrival_time")
    private Instant estimatedArrivalTime;

    @Column(name = "actual_arrival_time")
    private Instant actualArrivalTime;

    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    // Constructors
    public RescueWorkflow() {
    }

    public RescueWorkflow(RescueRequest rescueRequest) {
        this.rescueRequest = rescueRequest;
        this.createdAt = Instant.now();
        this.workflowStatus = WorkflowStatus.PENDING;
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

    public Account getCompany() {
        return company;
    }

    public void setCompany(Account company) {
        this.company = company;
    }

    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getProcessingSteps() {
        return processingSteps;
    }

    public void setProcessingSteps(String processingSteps) {
        this.processingSteps = processingSteps;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(Instant estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Instant getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(Instant actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public String getCompletionNotes() {
        return completionNotes;
    }

    public void setCompletionNotes(String completionNotes) {
        this.completionNotes = completionNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
