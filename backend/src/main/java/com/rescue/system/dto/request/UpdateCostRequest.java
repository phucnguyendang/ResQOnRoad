package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for updating agreed cost in a conversation
 * UC101 - Step 6 & 7: Cost negotiation and recording
 */
public class UpdateCostRequest {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotNull(message = "Agreed cost is required")
    @Positive(message = "Cost must be positive")
    private Double agreedCost;

    private String costNotes;

    // Constructors
    public UpdateCostRequest() {
    }

    public UpdateCostRequest(Long requestId, Double agreedCost) {
        this.requestId = requestId;
        this.agreedCost = agreedCost;
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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
}
