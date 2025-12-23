package com.rescue.system.dto.response;

import com.rescue.system.entity.RescueRequestTracking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingDto {
    
    private Long id;
    private Long rescueRequestId;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long updatedBy;
    private String updatedByName;
    private LocalDateTime updatedAt;
    
    public static TrackingDto fromEntity(RescueRequestTracking tracking, String updatedByName) {
        return TrackingDto.builder()
                .id(tracking.getId())
                .rescueRequestId(tracking.getRescueRequest().getId())
                .latitude(tracking.getLatitude())
                .longitude(tracking.getLongitude())
                .address(tracking.getAddress())
                .updatedBy(tracking.getUpdatedBy())
                .updatedByName(updatedByName)
                .updatedAt(tracking.getUpdatedAt())
                .build();
    }
}
