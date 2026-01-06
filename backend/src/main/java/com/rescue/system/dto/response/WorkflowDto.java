package com.rescue.system.dto.response;

import com.rescue.system.entity.RescueWorkflow;
import com.rescue.system.entity.WorkflowStatus;
import java.time.Instant;
import java.util.List;

/**
 * DTO phản hồi thông tin quy trình cứu hộ
 */
public class WorkflowDto {

    private Long id;
    private Long rescueRequestId;
    private Long companyId;
    private String companyName;
    private WorkflowStatus workflowStatus;
    private String workflowStatusDisplay;
    private String processingSteps;
    private String currentStep;
    private Integer stepNumber;
    private String notes;
    private Instant estimatedArrivalTime;
    private Instant actualArrivalTime;
    private String completionNotes;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant startedAt;
    private Instant completedAt;

    // Thông tin yêu cầu cứu hộ liên quan
    private String userFullName;
    private String userPhone;
    private String location;
    private Double latitude;
    private Double longitude;
    private String description;
    private String serviceType;

    // Danh sách các bước xử lý
    private List<WorkflowStepDto> steps;

    public WorkflowDto() {
    }

    public WorkflowDto(RescueWorkflow workflow) {
        this.id = workflow.getId();
        this.rescueRequestId = workflow.getRescueRequest().getId();

        if (workflow.getCompany() != null) {
            this.companyId = workflow.getCompany().getId();
            this.companyName = workflow.getCompany().getFullName();
        }

        this.workflowStatus = workflow.getWorkflowStatus();
        this.workflowStatusDisplay = workflow.getWorkflowStatus().getDisplayName();
        this.processingSteps = workflow.getProcessingSteps();
        this.currentStep = workflow.getCurrentStep();
        this.stepNumber = workflow.getStepNumber();
        this.notes = workflow.getNotes();
        this.estimatedArrivalTime = workflow.getEstimatedArrivalTime();
        this.actualArrivalTime = workflow.getActualArrivalTime();
        this.completionNotes = workflow.getCompletionNotes();
        this.createdAt = workflow.getCreatedAt();
        this.updatedAt = workflow.getUpdatedAt();
        this.startedAt = workflow.getStartedAt();
        this.completedAt = workflow.getCompletedAt();

        // Lấy thông tin từ rescue request
        if (workflow.getRescueRequest() != null) {
            var request = workflow.getRescueRequest();
            this.userFullName = request.getUser().getFullName();
            this.userPhone = request.getUser().getPhoneNumber();
            this.location = request.getLocation();
            this.latitude = request.getLatitude();
            this.longitude = request.getLongitude();
            this.description = request.getDescription();
            this.serviceType = request.getServiceType();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRescueRequestId() {
        return rescueRequestId;
    }

    public void setRescueRequestId(Long rescueRequestId) {
        this.rescueRequestId = rescueRequestId;
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

    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getWorkflowStatusDisplay() {
        return workflowStatusDisplay;
    }

    public void setWorkflowStatusDisplay(String workflowStatusDisplay) {
        this.workflowStatusDisplay = workflowStatusDisplay;
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

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public List<WorkflowStepDto> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStepDto> steps) {
        this.steps = steps;
    }
}
