package com.rescue.system.dto.response;

import com.rescue.system.entity.RescueRequest;
import com.rescue.system.entity.RescueStatusHistory;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UpdateRescueStatusResponseDto {

    private Long request_id;
    private String current_status;
    private String updated_at;
    private List<StatusHistoryItemDto> history;

    public UpdateRescueStatusResponseDto() {
    }

    public UpdateRescueStatusResponseDto(RescueRequest request, List<RescueStatusHistory> statusHistories) {
        this.request_id = request.getId();
        this.current_status = request.getStatus().name();
        this.updated_at = request.getUpdatedAt() != null ? request.getUpdatedAt().toString() : null;
        this.history = new ArrayList<>();

        if (statusHistories != null) {
            for (RescueStatusHistory h : statusHistories) {
                LocalTime time = h.getChangedAt().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                this.history.add(new StatusHistoryItemDto(h.getNewStatus().name(), time.toString()));
            }
        }
    }

    // Getters and Setters
    public Long getRequest_id() {
        return request_id;
    }

    public void setRequest_id(Long request_id) {
        this.request_id = request_id;
    }

    public String getCurrent_status() {
        return current_status;
    }

    public void setCurrent_status(String current_status) {
        this.current_status = current_status;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<StatusHistoryItemDto> getHistory() {
        return history;
    }

    public void setHistory(List<StatusHistoryItemDto> history) {
        this.history = history;
    }

    /**
     * Inner class for status history item
     */
    public static class StatusHistoryItemDto {
        private String status;
        private String time;

        public StatusHistoryItemDto() {
        }

        public StatusHistoryItemDto(String status, String time) {
            this.status = status;
            this.time = time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
