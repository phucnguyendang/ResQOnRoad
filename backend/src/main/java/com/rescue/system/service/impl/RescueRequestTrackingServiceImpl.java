package com.rescue.system.service.impl;

import com.rescue.system.dto.request.CreateTrackingDto;
import com.rescue.system.dto.response.TrackingDto;
import com.rescue.system.entity.RescueRequest;
import com.rescue.system.entity.RescueRequestTracking;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.RescueRequestRepository;
import com.rescue.system.repository.RescueRequestTrackingRepository;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.service.RescueRequestTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RescueRequestTrackingServiceImpl implements RescueRequestTrackingService {
    
    private final RescueRequestTrackingRepository trackingRepository;
    private final RescueRequestRepository rescueRequestRepository;
    private final AccountRepository accountRepository;
    
    @Override
    public TrackingDto updateTracking(Long rescueRequestId, Long companyId, CreateTrackingDto dto) {
        // Kiểm tra yêu cầu cứu hộ tồn tại
        RescueRequest rescueRequest = rescueRequestRepository.findById(rescueRequestId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Yêu cầu cứu hộ không tồn tại"
                ));
        
        // Kiểm tra company có quyền cập nhật không
        Long requestCompanyId = rescueRequest.getCompany() != null ? rescueRequest.getCompany().getId() : null;
        if (requestCompanyId == null || !requestCompanyId.equals(companyId)) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "Company không được phép cập nhật tracking cho yêu cầu này"
            );
        }
        
        // Kiểm tra yêu cầu đang được company xử lý (trạng thái ACCEPTED, IN_TRANSIT, IN_PROGRESS)
        String status = rescueRequest.getStatus().toString();
        if (!status.equals("ACCEPTED") && !status.equals("IN_TRANSIT") && !status.equals("IN_PROGRESS")) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ có thể cập nhật tracking khi yêu cầu đang được xử lý (ACCEPTED, IN_TRANSIT, IN_PROGRESS)"
            );
        }
        
        // Tạo tracking mới
        RescueRequestTracking tracking = new RescueRequestTracking();
        tracking.setRescueRequest(rescueRequest);
        tracking.setLatitude(dto.getLatitude());
        tracking.setLongitude(dto.getLongitude());
        tracking.setAddress(dto.getAddress());
        tracking.setUpdatedBy(companyId);
        
        RescueRequestTracking saved = trackingRepository.save(tracking);
        
        // Lấy tên company
        String companyName = accountRepository.findById(companyId)
                .map(acc -> acc.getFullName())
                .orElse("Unknown Company");
        
        return TrackingDto.fromEntity(saved, companyName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrackingDto> getTrackingHistory(Long rescueRequestId) {
        // Kiểm tra yêu cầu tồn tại
        if (!rescueRequestRepository.existsById(rescueRequestId)) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "Yêu cầu cứu hộ không tồn tại"
            );
        }
        
        List<RescueRequestTracking> trackingList = trackingRepository
                .findByRescueRequestIdOrderByUpdatedAtDesc(rescueRequestId);
        
        return trackingList.stream()
                .map(tracking -> {
                    String companyName = accountRepository.findById(tracking.getUpdatedBy())
                            .map(acc -> acc.getFullName())
                            .orElse("Unknown Company");
                    return TrackingDto.fromEntity(tracking, companyName);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrackingDto getLatestTracking(Long rescueRequestId) {
        // Kiểm tra yêu cầu tồn tại
        if (!rescueRequestRepository.existsById(rescueRequestId)) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "Yêu cầu cứu hộ không tồn tại"
            );
        }
        
        RescueRequestTracking tracking = trackingRepository
                .findLatestTracking(rescueRequestId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Chưa có dữ liệu tracking cho yêu cầu này"
                ));
        
        String companyName = accountRepository.findById(tracking.getUpdatedBy())
                .map(acc -> acc.getFullName())
                .orElse("Unknown Company");
        
        return TrackingDto.fromEntity(tracking, companyName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrackingDto> getCompanyTrackingHistory(Long rescueRequestId, Long companyId) {
        // Kiểm tra yêu cầu tồn tại
        RescueRequest rescueRequest = rescueRequestRepository.findById(rescueRequestId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Yêu cầu cứu hộ không tồn tại"
                ));
        
        // Kiểm tra company có quyền xem không
        Long requestCompanyId = rescueRequest.getCompany() != null ? rescueRequest.getCompany().getId() : null;
        if (requestCompanyId == null || !requestCompanyId.equals(companyId)) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "Company không có quyền xem tracking cho yêu cầu này"
            );
        }
        
        List<RescueRequestTracking> trackingList = trackingRepository
                .findByRescueRequestIdAndUpdatedByOrderByUpdatedAtDesc(rescueRequestId, companyId);
        
        String companyName = accountRepository.findById(companyId)
                .map(acc -> acc.getFullName())
                .orElse("Unknown Company");
        
        final String finalCompanyName = companyName;
        return trackingList.stream()
                .map(tracking -> TrackingDto.fromEntity(tracking, finalCompanyName))
                .collect(Collectors.toList());
    }
}
