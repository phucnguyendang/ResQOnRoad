package com.rescue.system.service;

import com.rescue.system.dto.request.CreateTrackingDto;
import com.rescue.system.dto.response.TrackingDto;
import java.util.List;

public interface RescueRequestTrackingService {
    
    /**
     * Company cập nhật vị trí hiện tại
     */
    TrackingDto updateTracking(Long rescueRequestId, Long companyId, CreateTrackingDto dto);
    
    /**
     * Lấy lịch sử tracking của yêu cầu cứu hộ
     */
    List<TrackingDto> getTrackingHistory(Long rescueRequestId);
    
    /**
     * Lấy vị trí GPS mới nhất
     */
    TrackingDto getLatestTracking(Long rescueRequestId);
    
    /**
     * Lấy lịch sử tracking của company trong yêu cầu
     */
    List<TrackingDto> getCompanyTrackingHistory(Long rescueRequestId, Long companyId);
}
