package com.rescue.system.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity lưu trữ chi tiết từng bước xử lý trong quy trình cứu hộ
 */
@Entity
@Table(name = "workflow_steps", indexes = {
        @Index(name = "idx_step_workflow_id", columnList = "workflow_id"),
        @Index(name = "idx_step_number", columnList = "step_number")
})
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private RescueWorkflow workflow;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;

    @Column(name = "step_description", columnDefinition = "TEXT")
    private String stepDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_status", nullable = false, length = 30)
    private StepStatus stepStatus = StepStatus.PENDING;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    // Constructors
    public WorkflowStep() {
    }

    public WorkflowStep(RescueWorkflow workflow, Integer stepNumber, String stepName, String stepDescription) {
        this.workflow = workflow;
        this.stepNumber = stepNumber;
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.stepStatus = StepStatus.PENDING;
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(RescueWorkflow workflow) {
        this.workflow = workflow;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public StepStatus getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(StepStatus stepStatus) {
        this.stepStatus = stepStatus;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
