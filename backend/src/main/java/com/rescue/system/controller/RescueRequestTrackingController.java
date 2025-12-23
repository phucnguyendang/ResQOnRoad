package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateTrackingDto;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.TrackingDto;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.RescueRequestTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class RescueRequestTrackingController {
    
    private final RescueRequestTrackingService trackingService;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Company cập nhật vị trí hiện tại
     * POST /api/tracking/{requestId}/update
     */
    @PostMapping("/{requestId}/update")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<TrackingDto>> updateTracking(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateTrackingDto dto,
            @RequestHeader("Authorization") String authHeader) {
        
        Long companyId = getUserIdFromToken(authHeader);
        TrackingDto tracking = trackingService.updateTracking(requestId, companyId, dto);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Cập nhật vị trí thành công", tracking));
    }
    
    /**
     * Lấy lịch sử tracking của yêu cầu cứu hộ
     * GET /api/tracking/{requestId}
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<List<TrackingDto>>> getTrackingHistory(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String authHeader) {
        
        List<TrackingDto> trackingHistory = trackingService.getTrackingHistory(requestId);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>("Lấy lịch sử tracking thành công", trackingHistory));
    }
    
    /**
     * Lấy vị trí GPS mới nhất
     * GET /api/tracking/{requestId}/latest
     */
    @GetMapping("/{requestId}/latest")
    public ResponseEntity<ApiResponse<TrackingDto>> getLatestTracking(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String authHeader) {
        
        TrackingDto latestTracking = trackingService.getLatestTracking(requestId);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>("Lấy vị trí mới nhất thành công", latestTracking));
    }
    
    /**
     * Lấy lịch sử tracking của company trong yêu cầu (chỉ company tương ứng)
     * GET /api/tracking/{requestId}/company-history
     */
    @GetMapping("/{requestId}/company-history")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<List<TrackingDto>>> getCompanyTrackingHistory(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String authHeader) {
        
        Long companyId = getUserIdFromToken(authHeader);
        List<TrackingDto> trackingHistory = trackingService.getCompanyTrackingHistory(requestId, companyId);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>("Lấy lịch sử tracking của company thành công", trackingHistory));
    }
    
    /**
     * Lấy user ID từ JWT token
     */
    private Long getUserIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
