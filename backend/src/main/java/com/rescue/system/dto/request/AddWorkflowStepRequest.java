package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO để thêm bước xử lý mới vào quy trình
 */
public class AddWorkflowStepRequest {

    @NotBlank(message = "Tên bước không được để trống")
    private String stepName;

    private String stepDescription;

    private String performedBy;

    public AddWorkflowStepRequest() {
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

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
}
