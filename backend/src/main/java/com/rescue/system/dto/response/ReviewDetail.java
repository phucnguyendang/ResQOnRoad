package com.rescue.system.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReviewDetail {
    private Long id;
    private String userName;
    private Integer rating;
    private String comment;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    // Constructors
    public ReviewDetail() {
    }

    public ReviewDetail(Long id, String userName, Integer rating, String comment, Boolean isVerified,
            LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    // Static builder method
    public static ReviewDetailBuilder builder() {
        return new ReviewDetailBuilder();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ReviewDetail that = (ReviewDetail) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, rating);
    }

    @Override
    public String toString() {
        return "ReviewDetail{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                '}';
    }

    // Builder class
    public static class ReviewDetailBuilder {
        private Long id;
        private String userName;
        private Integer rating;
        private String comment;
        private Boolean isVerified;
        private LocalDateTime createdAt;

        ReviewDetailBuilder() {
        }

        public ReviewDetailBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReviewDetailBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public ReviewDetailBuilder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public ReviewDetailBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public ReviewDetailBuilder isVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public ReviewDetailBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReviewDetail build() {
            return new ReviewDetail(id, userName, rating, comment, isVerified, createdAt);
        }
    }
}
