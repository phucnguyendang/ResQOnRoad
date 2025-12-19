package com.rescue.system.dto.response;

public class JwtResponse {

    private String token;
    private Long account_id;
    private String role;
    private Profile profile;

    public JwtResponse() {
    }

    public JwtResponse(String token, Long accountId, String role, Profile profile) {
        this.token = token;
        this.account_id = accountId;
        this.role = role;
        this.profile = profile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public static class Profile {
        private String full_name;
        private String avatar_base64;

        public Profile() {
        }

        public Profile(String fullName, String avatarBase64) {
            this.full_name = fullName;
            this.avatar_base64 = avatarBase64;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getAvatar_base64() {
            return avatar_base64;
        }

        public void setAvatar_base64(String avatar_base64) {
            this.avatar_base64 = avatar_base64;
        }
    }
}
