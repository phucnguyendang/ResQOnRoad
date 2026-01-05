package com.rescue.system.service;

import com.rescue.system.dto.request.UpdateCompanyProfileRequest;
import com.rescue.system.dto.response.CompanyProfileDto;

/**
 * Service interface for company profile management
 * Based on UC404 - Company Profile Management
 */
public interface CompanyProfileService {

    /**
     * UC404 - Step 2: Get company profile
     * Lấy thông tin hồ sơ công ty hiện tại
     *
     * @param accountId ID của tài khoản công ty
     * @return Company profile DTO
     */
    CompanyProfileDto getCompanyProfile(Long accountId);

    /**
     * UC404 - Step 3, 4, 5: Update company profile
     * Cập nhật thông tin hồ sơ công ty
     *
     * @param accountId ID của tài khoản công ty
     * @param request   Thông tin cập nhật
     * @return Updated company profile DTO
     */
    CompanyProfileDto updateCompanyProfile(Long accountId, UpdateCompanyProfileRequest request);

    /**
     * Validate license information
     * UC404 - Step 4a: Kiểm tra giấy phép hợp lệ
     *
     * @param businessLicense   Số giấy phép
     * @param licenseExpiryDate Ngày hết hạn
     * @return true nếu hợp lệ
     */
    boolean validateLicense(String businessLicense, java.time.LocalDateTime licenseExpiryDate);

    /**
     * Check if profile update requires admin approval
     * UC404 - Step 4b: Kiểm tra nếu cần admin phê duyệt
     *
     * @param currentProfile Hồ sơ hiện tại
     * @param request        Yêu cầu cập nhật
     * @return true nếu cần phê duyệt
     */
    boolean requiresApproval(CompanyProfileDto currentProfile, UpdateCompanyProfileRequest request);

    /**
     * Get company profile by company ID
     * Lấy thông tin hồ sơ công ty theo ID công ty
     *
     * @param companyId ID của công ty
     * @return Company profile DTO
     */
    CompanyProfileDto getCompanyProfileById(Long companyId);
}
