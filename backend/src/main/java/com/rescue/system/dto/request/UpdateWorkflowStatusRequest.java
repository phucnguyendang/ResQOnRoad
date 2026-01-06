package com.rescue.system.dto.request;

import com.rescue.system.entity.WorkflowStatus;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * DTO để cập nhật trạng thái quy trình cứu hộ
 */
public class UpdateWorkflowStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private WorkflowStatus workflowStatus;

    private String notes;

    private Instant estimatedArrivalTime;

    public UpdateWorkflowStatusRequest() {
    }

    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
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
}
