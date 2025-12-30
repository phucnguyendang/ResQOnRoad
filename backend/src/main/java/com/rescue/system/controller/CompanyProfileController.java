package com.rescue.system.controller;

import com.rescue.system.dto.request.UpdateCompanyProfileRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.CompanyProfileDto;
import com.rescue.system.entity.ProfileStatus;
import com.rescue.system.exception.ApiException;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.CompanyProfileService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for company profile management
 * UC404 - Quản lý hồ sơ công ty cứu hộ
 * 
 * APIs:
 * - GET /api/companies/profile - Get current company profile
 * - PUT /api/companies/profile - Update company profile
 * - GET /api/companies/{companyId}/profile - Get company profile by ID (public)
 */
@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CompanyProfileController {

    @Autowired
    private CompanyProfileService companyProfileService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * UC404 - Step 2: Get current company profile
     * GET /api/companies/profile
     * 
     * Hiển thị hồ sơ hiện tại của công ty: tên doanh nghiệp, MST, địa chỉ, 
     * giấy phép, thông tin liên hệ, dịch vụ cung cấp, phạm vi hoạt động...
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> getCompanyProfile(
            @RequestHeader("Authorization") String token) {
        try {
            Long accountId = getAccountIdFromToken(token);
            CompanyProfileDto result = companyProfileService.getCompanyProfile(accountId);

            ApiResponse<CompanyProfileDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy thông tin hồ sơ công ty thành công",
                    result
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Lỗi khi lấy thông tin hồ sơ: " + e.getMessage());
        }
    }

    /**
     * UC404 - Step 3, 4, 5: Update company profile
     * PUT /api/companies/profile
     * 
     * Chỉnh sửa các thông tin cho phép (địa chỉ, số điện thoại, dịch vụ).
     * Có thể tải lên tài liệu hoặc giấy phép mới.
     * 
     * Response includes:
     * - Updated profile
     * - Warning if approval is needed (status = PENDING_APPROVAL)
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> updateCompanyProfile(
            @Valid @RequestBody UpdateCompanyProfileRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long accountId = getAccountIdFromToken(token);
            CompanyProfileDto result = companyProfileService.updateCompanyProfile(accountId, request);

            // Determine appropriate message based on profile status
            String message;
            if (result.getProfileStatus() == ProfileStatus.PENDING_APPROVAL) {
                message = "Cập nhật hồ sơ thành công. Một số thay đổi cần được admin phê duyệt trước khi có hiệu lực.";
            } else {
                message = "Cập nhật hồ sơ công ty thành công";
            }

            ApiResponse<CompanyProfileDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    message,
                    result
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
        }
    }

    /**
     * Get company profile by ID (public endpoint for viewing)
     * GET /api/companies/{companyId}/profile
     * 
     * Allows users to view a company's public profile
     */
    @GetMapping("/{companyId}/profile")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> getCompanyProfileById(
            @PathVariable Long companyId) {
        try {
            CompanyProfileDto result = companyProfileService.getCompanyProfileById(companyId);

            ApiResponse<CompanyProfileDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy thông tin hồ sơ công ty thành công",
                    result
            );
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Lỗi khi lấy thông tin hồ sơ: " + e.getMessage());
        }
    }

    /**
     * Extract account ID from JWT token
     */
    private Long getAccountIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtTokenProvider.parseClaims(token);
            return claims.get("account_id", Long.class);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn");
        }
    }
}
