package com.rescue.system.service;

import com.rescue.system.dto.request.CreateRescueRequestDto;
import com.rescue.system.dto.request.RejectRescueRequestDto;
import com.rescue.system.dto.request.UpdateRescueStatusDto;
import com.rescue.system.dto.response.RescueRequestDto;
import com.rescue.system.dto.response.UpdateRescueStatusResponseDto;
import com.rescue.system.entity.RescueStatus;

import java.util.List;

public interface RescueRequestService {
    
    /**
     * Create a new rescue request
     */
    RescueRequestDto createRescueRequest(Long userId, CreateRescueRequestDto requestDto);
    
    /**
     * Get rescue request by ID
     */
    RescueRequestDto getRescueRequestById(Long requestId);
    
    /**
     * Get all rescue requests for a user
     */
    List<RescueRequestDto> getRescueRequestsByUserId(Long userId);
    
    /**
     * Get all rescue requests assigned to a company
     */
    List<RescueRequestDto> getRescueRequestsByCompanyId(Long companyId);
    
    /**
     * Get all rescue requests by status
     */
    List<RescueRequestDto> getRescueRequestsByStatus(RescueStatus status);
    
    /**
     * Accept rescue request - assign to company and change status to ACCEPTED
     */
    RescueRequestDto acceptRescueRequest(Long requestId, Long companyId);
    
    /**
     * Update rescue request status by company
     */
    RescueRequestDto updateRescueRequestStatus(Long requestId, Long companyId, UpdateRescueStatusDto statusDto);
    
    /**
     * Update rescue request status and return response with history
     */
    UpdateRescueStatusResponseDto updateRescueRequestStatusWithHistory(Long requestId, Long companyId, UpdateRescueStatusDto statusDto);
    
    /**
     * Reject rescue request with reason
     */
    RescueRequestDto rejectRescueRequest(Long requestId, Long companyId, RejectRescueRequestDto rejectDto);
    
    /**
     * Cancel rescue request by user
     */
    RescueRequestDto cancelRescueRequest(Long requestId, Long userId);
    
    /**
     * Verify if a rescue request can be updated by a company
     */
    boolean canCompanyUpdateRequest(Long requestId, Long companyId);
}
