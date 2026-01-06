package com.rescue.system.dto.response;

import java.util.Objects;

public class PublicUserProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String avatarBase64;
    private String role;
    private Long companyId;

    public PublicUserProfileResponse() {
    }

    public PublicUserProfileResponse(Long id, String username, String fullName, String avatarBase64, String role, Long companyId) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.avatarBase64 = avatarBase64;
        this.role = role;
        this.companyId = companyId;
    }

    public static PublicUserProfileResponseBuilder builder() {
        return new PublicUserProfileResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicUserProfileResponse that = (PublicUserProfileResponse) o;
        return Objects.equals(id, that.id)
                && Objects.equals(username, that.username)
                && Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, fullName);
    }

    @Override
    public String toString() {
        return "PublicUserProfileResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", companyId=" + companyId +
                "}";
    }

    public static class PublicUserProfileResponseBuilder {
        private Long id;
        private String username;
        private String fullName;
        private String avatarBase64;
        private String role;
        private Long companyId;

        PublicUserProfileResponseBuilder() {
        }

        public PublicUserProfileResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PublicUserProfileResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public PublicUserProfileResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public PublicUserProfileResponseBuilder avatarBase64(String avatarBase64) {
            this.avatarBase64 = avatarBase64;
            return this;
        }

        public PublicUserProfileResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public PublicUserProfileResponseBuilder companyId(Long companyId) {
            this.companyId = companyId;
            return this;
        }

        public PublicUserProfileResponse build() {
            return new PublicUserProfileResponse(id, username, fullName, avatarBase64, role, companyId);
        }

        @Override
        public String toString() {
            return "PublicUserProfileResponse.PublicUserProfileResponseBuilder{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", role='" + role + '\'' +
                    ", companyId=" + companyId +
                    "}";
        }
    }
}
