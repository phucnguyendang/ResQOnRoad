package com.rescue.system.dto.response;

import com.rescue.system.entity.StepStatus;
import com.rescue.system.entity.WorkflowStep;
import java.time.Instant;

/**
 * DTO phản hồi thông tin từng bước trong quy trình cứu hộ
 */
public class WorkflowStepDto {

    private Long id;
    private Long workflowId;
    private Integer stepNumber;
    private String stepName;
    private String stepDescription;
    private StepStatus stepStatus;
    private String stepStatusDisplay;
    private String performedBy;
    private String notes;
    private Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;

    public WorkflowStepDto() {
    }

    public WorkflowStepDto(WorkflowStep step) {
        this.id = step.getId();
        this.workflowId = step.getWorkflow().getId();
        this.stepNumber = step.getStepNumber();
        this.stepName = step.getStepName();
        this.stepDescription = step.getStepDescription();
        this.stepStatus = step.getStepStatus();
        this.stepStatusDisplay = step.getStepStatus().getDisplayName();
        this.performedBy = step.getPerformedBy();
        this.notes = step.getNotes();
        this.createdAt = step.getCreatedAt();
        this.startedAt = step.getStartedAt();
        this.completedAt = step.getCompletedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
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

    public String getStepStatusDisplay() {
        return stepStatusDisplay;
    }

    public void setStepStatusDisplay(String stepStatusDisplay) {
        this.stepStatusDisplay = stepStatusDisplay;
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
