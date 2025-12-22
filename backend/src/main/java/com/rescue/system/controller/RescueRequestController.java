package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateRescueRequestDto;
import com.rescue.system.dto.request.RejectRescueRequestDto;
import com.rescue.system.dto.request.UpdateRescueStatusDto;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.RescueRequestDto;
import com.rescue.system.entity.RescueStatus;
import com.rescue.system.exception.ApiException;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.RescueRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/rescue-requests")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RescueRequestController {

    @Autowired
    private RescueRequestService rescueRequestService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * UC201: Create a new rescue request
     * POST /api/rescue-requests
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<RescueRequestDto>> createRescueRequest(
            @Valid @RequestBody CreateRescueRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            RescueRequestDto result = rescueRequestService.createRescueRequest(userId, requestDto);
            
            ApiResponse<RescueRequestDto> response = new ApiResponse<>(
                    "Yêu cầu cứu hộ đã được tạo thành công",
                    result
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create rescue request: " + e.getMessage());
        }
    }

    /**
     * Get rescue request details by ID
     * GET /api/rescue-requests/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<RescueRequestDto>> getRescueRequest(@PathVariable Long id) {
        try {
            RescueRequestDto result = rescueRequestService.getRescueRequestById(id);
            ApiResponse<RescueRequestDto> response = new ApiResponse<>(
                    "Lấy chi tiết yêu cầu cứu hộ thành công",
                    result
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get rescue request: " + e.getMessage());
        }
    }

    /**
     * Get all rescue requests for current user
     * GET /api/rescue-requests/user/my-requests
     */
    @GetMapping("/user/my-requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<RescueRequestDto>>> getUserRescueRequests(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            List<RescueRequestDto> results = rescueRequestService.getRescueRequestsByUserId(userId);
            
            ApiResponse<List<RescueRequestDto>> response = new ApiResponse<>(
                    "Lấy danh sách yêu cầu cứu hộ thành công",
                    results
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get user rescue requests: " + e.getMessage());
        }
    }

    /**
     * Get all rescue requests assigned to current company
     * GET /api/rescue-requests/company/assigned
     */
    @GetMapping("/company/assigned")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<List<RescueRequestDto>>> getCompanyRescueRequests(
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            List<RescueRequestDto> results = rescueRequestService.getRescueRequestsByCompanyId(companyId);
            
            ApiResponse<List<RescueRequestDto>> response = new ApiResponse<>(
                    "Lấy danh sách yêu cầu được giao thành công",
                    results
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get company rescue requests: " + e.getMessage());
        }
    }

    /**
     * Get rescue requests by status
     * GET /api/rescue-requests/status/{status}
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<RescueRequestDto>>> getRescueRequestsByStatus(
            @PathVariable String status) {
        try {
            RescueStatus rescueStatus = RescueStatus.valueOf(status);
            List<RescueRequestDto> results = rescueRequestService.getRescueRequestsByStatus(rescueStatus);
            
            ApiResponse<List<RescueRequestDto>> response = new ApiResponse<>(
                    "Lấy danh sách yêu cầu theo trạng thái thành công",
                    results
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get rescue requests by status: " + e.getMessage());
        }
    }

    /**
     * UC205: Accept rescue request (Company confirms to handle the request)
     * POST /api/rescue-requests/{id}/accept
     */
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<RescueRequestDto>> acceptRescueRequest(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            RescueRequestDto result = rescueRequestService.acceptRescueRequest(id, companyId);
            
            ApiResponse<RescueRequestDto> response = new ApiResponse<>(
                    "Yêu cầu cứu hộ đã được chấp nhận thành công",
                    result
            );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to accept rescue request: " + e.getMessage());
        }
    }

    /**
     * UC205: Update rescue request status (Company updates progress)
     * PATCH /api/rescue-requests/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<RescueRequestDto>> updateRescueRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRescueStatusDto statusDto,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            RescueRequestDto result = rescueRequestService.updateRescueRequestStatus(id, companyId, statusDto);
            
            ApiResponse<RescueRequestDto> response = new ApiResponse<>(
                    "Trạng thái yêu cầu cứu hộ đã được cập nhật thành công",
                    result
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update rescue request status: " + e.getMessage());
        }
    }

    /**
     * UC205: Reject rescue request (Company refuses to handle)
     * POST /api/rescue-requests/{id}/reject
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<RescueRequestDto>> rejectRescueRequest(
            @PathVariable Long id,
            @Valid @RequestBody RejectRescueRequestDto rejectDto,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            RescueRequestDto result = rescueRequestService.rejectRescueRequest(id, companyId, rejectDto);
            
            ApiResponse<RescueRequestDto> response = new ApiResponse<>(
                    "Yêu cầu cứu hộ đã được từ chối",
                    result
            );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reject rescue request: " + e.getMessage());
        }
    }

    /**
     * UC203: Cancel rescue request (User cancels their request)
     * POST /api/rescue-requests/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<RescueRequestDto>> cancelRescueRequest(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            RescueRequestDto result = rescueRequestService.cancelRescueRequest(id, userId);
            
            ApiResponse<RescueRequestDto> response = new ApiResponse<>(
                    "Yêu cầu cứu hộ đã được hủy",
                    result
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to cancel rescue request: " + e.getMessage());
        }
    }

    /**
     * Extract user ID from JWT token
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token format");
        }
        
        String jwtToken = token.substring(7);
        String username = jwtTokenProvider.getUsernameFromToken(jwtToken);
        
        if (username == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        
        // Extract user ID from token (assuming it's stored in claims)
        // This assumes JwtTokenProvider has a method to get userId
        // If not, you'll need to query the database by username
        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
        
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot extract user ID from token");
        }
        
        return userId;
    }
}
