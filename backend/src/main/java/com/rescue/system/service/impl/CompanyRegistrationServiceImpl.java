package com.rescue.system.service.impl;

import com.rescue.system.dto.request.CreateCompanyRegistrationRequest;
import com.rescue.system.dto.response.CompanyRegistrationResponse;
import com.rescue.system.entity.*;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.CompanyRegistrationRequestRepository;
import com.rescue.system.repository.RescueCompanyRepository;
import com.rescue.system.service.CompanyRegistrationService;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompanyRegistrationServiceImpl implements CompanyRegistrationService {

    private final CompanyRegistrationRequestRepository registrationRepository;
    private final AccountRepository accountRepository;
    private final RescueCompanyRepository rescueCompanyRepository;

    public CompanyRegistrationServiceImpl(
            CompanyRegistrationRequestRepository registrationRepository,
            AccountRepository accountRepository,
            RescueCompanyRepository rescueCompanyRepository
    ) {
        this.registrationRepository = registrationRepository;
        this.accountRepository = accountRepository;
        this.rescueCompanyRepository = rescueCompanyRepository;
    }

    @Override
    public CompanyRegistrationResponse submitRegistration(String username, CreateCompanyRegistrationRequest request) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));

        if (account.getRole() != Role.USER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Chỉ tài khoản USER mới có thể gửi đăng ký công ty");
        }
        if (account.isLocked()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa");
        }

        if (registrationRepository.existsByAccountIdAndStatus(account.getId(), CompanyRegistrationStatus.PENDING)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bạn đã có một đơn đăng ký đang chờ duyệt");
        }

        CompanyRegistrationRequest entity = new CompanyRegistrationRequest();
        entity.setAccountId(account.getId());
        entity.setStatus(CompanyRegistrationStatus.PENDING);

        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setServiceRadius(request.getServiceRadius());
        entity.setTaxCode(request.getTaxCode());
        entity.setHotline(request.getHotline());
        entity.setOperatingHours(request.getOperatingHours());
        entity.setBusinessLicense(request.getBusinessLicense());
        entity.setLicenseDocumentUrl(request.getLicenseDocumentUrl());
        entity.setDescription(request.getDescription());

        CompanyRegistrationRequest saved = registrationRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyRegistrationResponse getMyLatestRegistration(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));

        CompanyRegistrationRequest latest = registrationRepository
                .findTopByAccountIdOrderBySubmittedAtDesc(account.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Bạn chưa có đơn đăng ký nào"));

        return toResponse(latest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyRegistrationResponse> adminListRegistrations(String adminUsername, String status, Pageable pageable) {
        Account admin = accountRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Admin account not found"));
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        CompanyRegistrationStatus st;
        try {
            st = status == null || status.isBlank() ? CompanyRegistrationStatus.PENDING : CompanyRegistrationStatus.valueOf(status.toUpperCase());
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Status không hợp lệ (PENDING|APPROVED|REJECTED)");
        }

        Page<CompanyRegistrationRequest> page = registrationRepository.findByStatus(st, pageable);
        return new PageImpl<>(page.map(this::toResponse).getContent(), pageable, page.getTotalElements());
    }

    @Override
    public CompanyRegistrationResponse adminApprove(String adminUsername, Long registrationId) {
        Account admin = accountRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Admin account not found"));
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        CompanyRegistrationRequest req = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Đơn đăng ký không tồn tại"));

        if (req.getStatus() != CompanyRegistrationStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Chỉ có thể duyệt đơn đang chờ (PENDING)");
        }

        Account account = accountRepository.findById(req.getAccountId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));

        if (account.getRole() != Role.USER) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Tài khoản không ở trạng thái USER để chuyển sang COMPANY");
        }
        if (account.getCompanyId() != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Tài khoản đã được liên kết với công ty");
        }

        RescueCompany company = new RescueCompany();
        company.setName(req.getName());
        company.setAddress(req.getAddress());
        company.setPhone(req.getPhone());
        company.setEmail(req.getEmail());
        company.setLatitude(req.getLatitude());
        company.setLongitude(req.getLongitude());
        company.setServiceRadius(req.getServiceRadius() != null ? req.getServiceRadius() : 50.0);
        company.setTaxCode(req.getTaxCode());
        company.setHotline(req.getHotline());
        company.setOperatingHours(req.getOperatingHours());
        company.setBusinessLicense(req.getBusinessLicense());
        company.setLicenseDocumentUrl(req.getLicenseDocumentUrl());
        company.setDescription(req.getDescription());

        // Business rule: approve => appear in search
        company.setIsActive(true);
        company.setIsVerified(false);
        company.setProfileStatus(ProfileStatus.INCOMPLETE);
        company.setAverageRating(0.0);
        company.setTotalReviews(0);
        company.setRejectionReason(null);

        RescueCompany savedCompany = rescueCompanyRepository.save(company);

        // IMPORTANT: update role + companyId together to satisfy DB triggers.
        account.setRole(Role.COMPANY);
        account.setCompanyId(savedCompany.getId());
        accountRepository.save(account);

        req.setStatus(CompanyRegistrationStatus.APPROVED);
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewedByAccountId(admin.getId());
        req.setCreatedCompanyId(savedCompany.getId());
        req.setRejectionReason(null);

        CompanyRegistrationRequest savedReq = registrationRepository.save(req);
        return toResponse(savedReq);
    }

    @Override
    public CompanyRegistrationResponse adminReject(String adminUsername, Long registrationId, String reason) {
        Account admin = accountRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Admin account not found"));
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        CompanyRegistrationRequest req = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Đơn đăng ký không tồn tại"));

        if (req.getStatus() != CompanyRegistrationStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Chỉ có thể từ chối đơn đang chờ (PENDING)");
        }

        req.setStatus(CompanyRegistrationStatus.REJECTED);
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewedByAccountId(admin.getId());
        req.setRejectionReason(reason);

        CompanyRegistrationRequest saved = registrationRepository.save(req);
        return toResponse(saved);
    }

    private CompanyRegistrationResponse toResponse(CompanyRegistrationRequest e) {
        CompanyRegistrationResponse r = new CompanyRegistrationResponse();
        r.setId(e.getId());
        r.setStatus(e.getStatus());

        r.setName(e.getName());
        r.setAddress(e.getAddress());
        r.setPhone(e.getPhone());
        r.setEmail(e.getEmail());
        r.setLatitude(e.getLatitude());
        r.setLongitude(e.getLongitude());
        r.setServiceRadius(e.getServiceRadius());
        r.setTaxCode(e.getTaxCode());
        r.setHotline(e.getHotline());
        r.setOperatingHours(e.getOperatingHours());
        r.setBusinessLicense(e.getBusinessLicense());
        r.setLicenseDocumentUrl(e.getLicenseDocumentUrl());
        r.setDescription(e.getDescription());

        r.setRejectionReason(e.getRejectionReason());
        r.setCreatedCompanyId(e.getCreatedCompanyId());
        r.setReviewedByAccountId(e.getReviewedByAccountId());
        r.setReviewedAt(e.getReviewedAt());
        r.setSubmittedAt(e.getSubmittedAt());
        return r;
    }
}
