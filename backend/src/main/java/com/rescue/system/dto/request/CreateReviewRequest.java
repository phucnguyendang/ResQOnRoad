package com.rescue.system.dto.request;

import jakarta.validation.constraints.*;

public class CreateReviewRequest {

    @NotNull(message = "requestId không được để trống")
    private Long requestId;

    @NotNull(message = "rating không được để trống")
    @Min(value = 1, message = "rating phải từ 1 đến 5")
    @Max(value = 5, message = "rating phải từ 1 đến 5")
    private Integer rating;

    @Size(max = 1000, message = "comment không được vượt quá 1000 ký tự")
    private String comment;

    // Constructors
    public CreateReviewRequest() {
    }

    public CreateReviewRequest(Long requestId, Integer rating, String comment) {
        this.requestId = requestId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "CreateReviewRequest{" +
                "requestId=" + requestId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
