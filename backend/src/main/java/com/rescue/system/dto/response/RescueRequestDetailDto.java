package com.rescue.system.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Docs-aligned rescue request detail response (api_docs.md §4.3).
 */
public class RescueRequestDetailDto {

    private Long id;

    private UserInfo user;

    private IncidentInfo incident;

    /**
     * Uses backend enum names (e.g., PENDING_CONFIRMATION, IN_TRANSIT).
     */
    private String status;

    private CompanyInfo company;

    private List<TimelineItem> timeline = new ArrayList<>();

    public RescueRequestDetailDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public IncidentInfo getIncident() {
        return incident;
    }

    public void setIncident(IncidentInfo incident) {
        this.incident = incident;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CompanyInfo getCompany() {
        return company;
    }

    public void setCompany(CompanyInfo company) {
        this.company = company;
    }

    public List<TimelineItem> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimelineItem> timeline) {
        this.timeline = timeline;
    }

    public static class UserInfo {
        @JsonProperty("full_name")
        private String fullName;

        @JsonProperty("phone")
        private String phone;

        public UserInfo() {
        }

        public UserInfo(String fullName, String phone) {
            this.fullName = fullName;
            this.phone = phone;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class IncidentInfo {
        @JsonProperty("desc")
        private String desc;

        @JsonProperty("images_base64")
        private List<String> imagesBase64 = new ArrayList<>();

        @JsonProperty("address")
        private String address;

        public IncidentInfo() {
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public List<String> getImagesBase64() {
            return imagesBase64;
        }

        public void setImagesBase64(List<String> imagesBase64) {
            this.imagesBase64 = imagesBase64;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class CompanyInfo {
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("hotline")
        private String hotline;

        public CompanyInfo() {
        }

        public CompanyInfo(Long id, String name, String hotline) {
            this.id = id;
            this.name = name;
            this.hotline = hotline;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHotline() {
            return hotline;
        }

        public void setHotline(String hotline) {
            this.hotline = hotline;
        }
    }

    public static class TimelineItem {
        private String status;

        @JsonProperty("updated_at")
        private Instant updatedAt;

        private String note;

        public TimelineItem() {
        }

        public TimelineItem(String status, Instant updatedAt, String note) {
            this.status = status;
            this.updatedAt = updatedAt;
            this.note = note;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Instant getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
