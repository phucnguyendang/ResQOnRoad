package com.rescue.system.service.impl;

import com.rescue.system.dto.request.CreateCompanyAdminRequest;
import com.rescue.system.dto.request.UpdateCompanyAdminRequest;
import com.rescue.system.dto.request.UpdateCompanyStatusRequest;
import com.rescue.system.dto.response.CompanyAdminResponse;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.ProfileStatus;
import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.Role;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.RescueCompanyRepository;
import com.rescue.system.service.CompanyAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyAdminServiceImpl implements CompanyAdminService {

    private final RescueCompanyRepository companyRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyAdminServiceImpl(
            RescueCompanyRepository companyRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<CompanyAdminResponse> getAllCompanies(Pageable pageable) {
        Page<RescueCompany> companies = companyRepository.findAll(pageable);
        List<CompanyAdminResponse> responses = companies.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, companies.getTotalElements());
    }

    @Override
    public CompanyAdminResponse getCompanyDetail(Long companyId) {
        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Công ty không tồn tại"));
        
        CompanyAdminResponse response = convertToResponse(company);
        
        // Lấy thông tin tài khoản liên kết
        Account account = accountRepository.findFirstByCompanyIdAndRole(companyId, Role.COMPANY)
                .orElse(null);
        if (account != null) {
            response.setAccountUsername(account.getUsername());
            response.setAccountId(account.getId());
        }
        
        return response;
    }

    @Override
    public CompanyAdminResponse createCompany(CreateCompanyAdminRequest request) {
        // Kiểm tra username đã tồn tại
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username đã tồn tại");
        }

        // Tạo công ty
        RescueCompany company = new RescueCompany();
        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setLatitude(request.getLatitude());
        company.setLongitude(request.getLongitude());
        company.setServiceRadius(request.getServiceRadius() != null ? request.getServiceRadius() : 50.0);
        company.setTaxCode(request.getTaxCode());
        company.setHotline(request.getHotline());
        company.setOperatingHours(request.getOperatingHours());
        company.setLicenseDocumentUrl(request.getLicenseDocumentUrl());
        company.setDescription(request.getDescription());
        company.setBusinessLicense(request.getBusinessLicense());
        company.setIsActive(true);
        company.setIsVerified(false);
        company.setProfileStatus(ProfileStatus.INCOMPLETE);
        company.setAverageRating(0.0);
        company.setTotalReviews(0);

        RescueCompany savedCompany = companyRepository.save(company);

        // Tạo tài khoản liên kết
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.COMPANY);
        account.setFullName(request.getName());
        account.setEmail(request.getEmail());
        account.setPhoneNumber(request.getPhone());
        account.setCompanyId(savedCompany.getId());
        account.setLocked(false);

        Account savedAccount = accountRepository.save(account);

        CompanyAdminResponse response = convertToResponse(savedCompany);
        response.setAccountUsername(savedAccount.getUsername());
        response.setAccountId(savedAccount.getId());

        return response;
    }

    @Override
    public CompanyAdminResponse updateCompany(Long companyId, UpdateCompanyAdminRequest request) {
        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Công ty không tồn tại"));

        // Cập nhật các trường được cung cấp
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
        if (request.getLatitude() != null) {
            company.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            company.setLongitude(request.getLongitude());
        }
        if (request.getServiceRadius() != null) {
            company.setServiceRadius(request.getServiceRadius());
        }
        if (request.getTaxCode() != null) {
            company.setTaxCode(request.getTaxCode());
        }
        if (request.getHotline() != null) {
            company.setHotline(request.getHotline());
        }
        if (request.getOperatingHours() != null) {
            company.setOperatingHours(request.getOperatingHours());
        }
        if (request.getLicenseDocumentUrl() != null) {
            company.setLicenseDocumentUrl(request.getLicenseDocumentUrl());
        }
        if (request.getDescription() != null) {
            company.setDescription(request.getDescription());
        }
        if (request.getBusinessLicense() != null) {
            company.setBusinessLicense(request.getBusinessLicense());
        }

        RescueCompany updatedCompany = companyRepository.save(company);
        return convertToResponse(updatedCompany);
    }

    @Override
    public CompanyAdminResponse updateCompanyStatus(Long companyId, UpdateCompanyStatusRequest request) {
        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Công ty không tồn tại"));

        String status = request.getStatus().toUpperCase();
        
        switch (status) {
            case "ACTIVE":
                company.setIsActive(true);
                company.setRejectionReason(null);
                break;
            case "LOCKED":
                company.setIsActive(false);
                company.setRejectionReason(request.getReason());
                break;
            case "DELETED":
                // Soft delete: đánh dấu là không hoạt động
                company.setIsActive(false);
                company.setRejectionReason("Tài khoản đã bị xóa: " + request.getReason());
                break;
            default:
                throw new ApiException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ. Các giá trị: ACTIVE, LOCKED, DELETED");
        }

        RescueCompany updatedCompany = companyRepository.save(company);
        return convertToResponse(updatedCompany);
    }

    @Override
    public void deleteCompany(Long companyId) {
        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Công ty không tồn tại"));

        // Kiểm tra xem công ty có yêu cầu cứu hộ đang hoạt động không
        // Nếu có, không cho phép xóa

        // Xóa tài khoản liên kết
        Account account = accountRepository.findFirstByCompanyIdAndRole(companyId, Role.COMPANY)
                .orElse(null);
        if (account != null) {
            accountRepository.delete(account);
        }

        // Xóa công ty
        companyRepository.delete(company);
    }

    @Override
    public CompanyAdminResponse verifyCompany(Long companyId) {
        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Công ty không tồn tại"));

        // Kiểm tra thông tin công ty đầy đủ
        if (company.getBusinessLicense() == null || company.getBusinessLicense().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Giấy phép kinh doanh chưa được cung cấp");
        }
        if (company.getAddress() == null || company.getAddress().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Địa chỉ chưa được cung cấp");
        }
        if (company.getTaxCode() == null || company.getTaxCode().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã số thuế chưa được cung cấp");
        }

        company.setIsVerified(true);
        company.setProfileStatus(ProfileStatus.APPROVED);
        company.setRejectionReason(null);

        RescueCompany verifiedCompany = companyRepository.save(company);
        return convertToResponse(verifiedCompany);
    }

    @Override
    public CompanyAdminResponse rejectCompanyVerification(Long companyId, String reason) {
        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Công ty không tồn tại"));

        company.setIsVerified(false);
        company.setProfileStatus(ProfileStatus.REJECTED);
        company.setRejectionReason(reason);

        RescueCompany rejectedCompany = companyRepository.save(company);
        return convertToResponse(rejectedCompany);
    }

    private CompanyAdminResponse convertToResponse(RescueCompany company) {
        CompanyAdminResponse response = new CompanyAdminResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        response.setAddress(company.getAddress());
        response.setPhone(company.getPhone());
        response.setEmail(company.getEmail());
        response.setLatitude(company.getLatitude());
        response.setLongitude(company.getLongitude());
        response.setServiceRadius(company.getServiceRadius());
        response.setIsActive(company.getIsActive());
        response.setAverageRating(company.getAverageRating());
        response.setTotalReviews(company.getTotalReviews());
        response.setDescription(company.getDescription());
        response.setBusinessLicense(company.getBusinessLicense());
        response.setIsVerified(company.getIsVerified());
        response.setProfileStatus(company.getProfileStatus());
        response.setTaxCode(company.getTaxCode());
        response.setHotline(company.getHotline());
        response.setOperatingHours(company.getOperatingHours());
        response.setLicenseExpiryDate(company.getLicenseExpiryDate());
        response.setLicenseDocumentUrl(company.getLicenseDocumentUrl());
        response.setRejectionReason(company.getRejectionReason());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());
        return response;
    }
}
