package com.rescue.system.service.impl;

import com.rescue.system.dto.request.UpdateCompanyProfileRequest;
import com.rescue.system.dto.response.CompanyProfileDto;
import com.rescue.system.dto.response.ServiceSummary;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.ProfileStatus;
import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.Role;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.RescueCompanyRepository;
import com.rescue.system.service.CompanyProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CompanyProfileService
 * Handles all company profile management functionality for UC404
 */
@Service
@Transactional
public class CompanyProfileServiceImpl implements CompanyProfileService {

    @Autowired
    private RescueCompanyRepository rescueCompanyRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Fields that require admin approval when changed
    private static final List<String> APPROVAL_REQUIRED_FIELDS = List.of(
            "businessLicense", "taxCode", "name", "licenseDocumentUrl"
    );

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileDto getCompanyProfile(Long accountId) {
        // Get account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        // Verify account is a company
        if (account.getRole() != Role.COMPANY) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Tài khoản không phải là công ty cứu hộ");
        }

        // Get company ID from account
        Long companyId = account.getCompanyId();
        if (companyId == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Công ty chưa được thiết lập hồ sơ");
        }

        return getCompanyProfileById(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileDto getCompanyProfileById(Long companyId) {
        RescueCompany company = rescueCompanyRepository.findByIdWithServices(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy công ty cứu hộ"));

        return convertToCompanyProfileDto(company);
    }

    @Override
    public CompanyProfileDto updateCompanyProfile(Long accountId, UpdateCompanyProfileRequest request) {
        // Get account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        // Verify account is a company
        if (account.getRole() != Role.COMPANY) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Tài khoản không phải là công ty cứu hộ");
        }

        // Get company
        Long companyId = account.getCompanyId();
        if (companyId == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Công ty chưa được thiết lập hồ sơ");
        }

        RescueCompany company = rescueCompanyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy công ty cứu hộ"));

        // Validate license if provided
        if (request.getBusinessLicense() != null || request.getLicenseExpiryDate() != null) {
            String license = request.getBusinessLicense() != null 
                    ? request.getBusinessLicense() 
                    : company.getBusinessLicense();
            LocalDateTime expiryDate = request.getLicenseExpiryDate() != null 
                    ? request.getLicenseExpiryDate() 
                    : company.getLicenseExpiryDate();

            if (!validateLicense(license, expiryDate)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 
                        "Giấy phép không hợp lệ hoặc đã hết hạn. Vui lòng kiểm tra lại thông tin.");
            }
        }

        // Get current profile for comparison
        CompanyProfileDto currentProfile = convertToCompanyProfileDto(company);

        // Check if approval is required
        boolean needsApproval = requiresApproval(currentProfile, request);

        // Update fields
        updateCompanyFields(company, request);

        // Set profile status based on whether approval is needed
        if (needsApproval) {
            company.setProfileStatus(ProfileStatus.PENDING_APPROVAL);
            company.setRejectionReason(null); // Clear any previous rejection
        } else if (company.getProfileStatus() == ProfileStatus.REJECTED) {
            // If was rejected and now updating without approval-required fields,
            // keep it as pending until admin reviews
            company.setProfileStatus(ProfileStatus.PENDING_APPROVAL);
            company.setRejectionReason(null);
        }

        // Save changes
        company = rescueCompanyRepository.save(company);

        // Return updated profile
        CompanyProfileDto result = convertToCompanyProfileDto(company);
        
        return result;
    }

    @Override
    public boolean validateLicense(String businessLicense, LocalDateTime licenseExpiryDate) {
        // Check if license number is provided and not empty
        if (businessLicense == null || businessLicense.trim().isEmpty()) {
            return false;
        }

        // Check if expiry date is in the future
        if (licenseExpiryDate != null && licenseExpiryDate.isBefore(LocalDateTime.now())) {
            return false;
        }

        // Additional validation rules can be added here
        // e.g., license format validation, external API validation, etc.

        return true;
    }

    @Override
    public boolean requiresApproval(CompanyProfileDto currentProfile, UpdateCompanyProfileRequest request) {
        // Check if any approval-required fields are being changed

        // Business license change
        if (request.getBusinessLicense() != null && 
                !request.getBusinessLicense().equals(currentProfile.getBusinessLicense())) {
            return true;
        }

        // Tax code change
        if (request.getTaxCode() != null && 
                !request.getTaxCode().equals(currentProfile.getTaxCode())) {
            return true;
        }

        // Company name change
        if (request.getName() != null && 
                !request.getName().equals(currentProfile.getName())) {
            return true;
        }

        // License document change
        if (request.getLicenseDocumentUrl() != null && 
                !request.getLicenseDocumentUrl().equals(currentProfile.getLicenseDocumentUrl())) {
            return true;
        }

        return false;
    }

    // Helper methods

    private void updateCompanyFields(RescueCompany company, UpdateCompanyProfileRequest request) {
        if (request.getName() != null) {
            company.setName(request.getName());
        }
        if (request.getAddress() != null) {
            company.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            company.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            company.setEmail(request.getEmail());
        }
        if (request.getHotline() != null) {
            company.setHotline(request.getHotline());
        }
        if (request.getLatitude() != null) {
            company.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            company.setLongitude(request.getLongitude());
        }
        if (request.getServiceRadius() != null) {
            company.setServiceRadius(request.getServiceRadius());
        }
        if (request.getDescription() != null) {
            company.setDescription(request.getDescription());
        }
        if (request.getTaxCode() != null) {
            company.setTaxCode(request.getTaxCode());
        }
        if (request.getBusinessLicense() != null) {
            company.setBusinessLicense(request.getBusinessLicense());
        }
        if (request.getLicenseExpiryDate() != null) {
            company.setLicenseExpiryDate(request.getLicenseExpiryDate());
        }
        if (request.getLicenseDocumentUrl() != null) {
            company.setLicenseDocumentUrl(request.getLicenseDocumentUrl());
        }
        if (request.getOperatingHours() != null) {
            company.setOperatingHours(request.getOperatingHours());
        }
    }

    private CompanyProfileDto convertToCompanyProfileDto(RescueCompany company) {
        CompanyProfileDto dto = new CompanyProfileDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setPhone(company.getPhone());
        dto.setEmail(company.getEmail());
        dto.setHotline(company.getHotline());
        dto.setLatitude(company.getLatitude());
        dto.setLongitude(company.getLongitude());
        dto.setServiceRadius(company.getServiceRadius());
        dto.setIsActive(company.getIsActive());
        dto.setAverageRating(company.getAverageRating());
        dto.setTotalReviews(company.getTotalReviews());
        dto.setDescription(company.getDescription());
        dto.setTaxCode(company.getTaxCode());
        dto.setBusinessLicense(company.getBusinessLicense());
        dto.setLicenseExpiryDate(company.getLicenseExpiryDate());
        dto.setLicenseDocumentUrl(company.getLicenseDocumentUrl());
        dto.setIsVerified(company.getIsVerified());
        dto.setProfileStatus(company.getProfileStatus());
        dto.setRejectionReason(company.getRejectionReason());
        dto.setOperatingHours(company.getOperatingHours());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());

        // Convert services
        if (company.getServices() != null) {
            List<ServiceSummary> serviceSummaries = company.getServices().stream()
                    .map(service -> {
                        ServiceSummary summary = new ServiceSummary();
                        summary.setId(service.getId());
                        summary.setName(service.getName());
                        summary.setType(service.getType().name());
                        summary.setTypeDisplayName(service.getType().getDisplayName());
                        summary.setBasePrice(service.getBasePrice());
                        summary.setPriceUnit(service.getPriceUnit());
                        summary.setIsAvailable(service.getIsAvailable());
                        summary.setEstimatedTime(service.getEstimatedTime());
                        return summary;
                    })
                    .collect(Collectors.toList());
            dto.setServices(serviceSummaries);
        } else {
            dto.setServices(new ArrayList<>());
        }

        return dto;
    }
}
