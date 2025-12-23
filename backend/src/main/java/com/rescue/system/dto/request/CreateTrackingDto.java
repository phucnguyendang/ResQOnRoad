package com.rescue.system.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrackingDto {
    
    @NotNull(message = "Latitude không được để trống")
    @DecimalMin(value = "-90", message = "Latitude phải từ -90 đến 90")
    @DecimalMax(value = "90", message = "Latitude phải từ -90 đến 90")
    private Double latitude;
    
    @NotNull(message = "Longitude không được để trống")
    @DecimalMin(value = "-180", message = "Longitude phải từ -180 đến 180")
    @DecimalMax(value = "180", message = "Longitude phải từ -180 đến 180")
    private Double longitude;
    
    @Size(max = 500, message = "Địa chỉ không vượt quá 500 ký tự")
    private String address;
}
