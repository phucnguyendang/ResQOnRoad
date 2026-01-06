package com.rescue.system.dto.request;

import com.rescue.system.entity.StepStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO để cập nhật trạng thái bước xử lý
 */
public class UpdateWorkflowStepRequest {

    @NotNull(message = "Trạng thái bước không được để trống")
    private StepStatus stepStatus;

    private String notes;

    private String performedBy;

    public UpdateWorkflowStepRequest() {
    }

    public StepStatus getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(StepStatus stepStatus) {
        this.stepStatus = stepStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
}
