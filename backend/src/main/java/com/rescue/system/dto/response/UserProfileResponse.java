package com.rescue.system.dto.response;

import java.util.Objects;

public class UserProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatarBase64;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String username, String fullName, String phoneNumber, String email, String avatarBase64) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.avatarBase64 = avatarBase64;
    }

    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileResponse that = (UserProfileResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, fullName);
    }

    @Override
    public String toString() {
        return "UserProfileResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", avatarBase64='" + avatarBase64 + '\'' +
                '}';
    }

    public static class UserProfileResponseBuilder {
        private Long id;
        private String username;
        private String fullName;
        private String phoneNumber;
        private String email;
        private String avatarBase64;

        UserProfileResponseBuilder() {
        }

        public UserProfileResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserProfileResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserProfileResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserProfileResponseBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserProfileResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileResponseBuilder avatarBase64(String avatarBase64) {
            this.avatarBase64 = avatarBase64;
            return this;
        }

        public UserProfileResponse build() {
            return new UserProfileResponse(id, username, fullName, phoneNumber, email, avatarBase64);
        }
    }
}