package com.rescue.system.controller;

import com.rescue.system.dto.request.RejectCompanyRegistrationRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.CompanyRegistrationResponse;
import com.rescue.system.exception.ApiException;
import com.rescue.system.service.CompanyRegistrationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/company-registrations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCompanyRegistrationController {

    private final CompanyRegistrationService companyRegistrationService;

    public AdminCompanyRegistrationController(CompanyRegistrationService companyRegistrationService) {
        this.companyRegistrationService = companyRegistrationService;
    }

    @GetMapping
    public ApiResponse<Page<CompanyRegistrationResponse>> list(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyRegistrationResponse> data = companyRegistrationService.adminListRegistrations(user.getUsername(), status, pageable);
        return ApiResponse.of("Lấy danh sách đơn đăng ký công ty thành công", data);
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<CompanyRegistrationResponse> approve(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        CompanyRegistrationResponse data = companyRegistrationService.adminApprove(user.getUsername(), id);
        return ApiResponse.of("Duyệt đơn đăng ký công ty thành công", data);
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<CompanyRegistrationResponse> reject(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody RejectCompanyRegistrationRequest request
    ) {
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        CompanyRegistrationResponse data = companyRegistrationService.adminReject(user.getUsername(), id, request.getReason());
        return ApiResponse.of("Từ chối đơn đăng ký công ty thành công", data);
    }
}
