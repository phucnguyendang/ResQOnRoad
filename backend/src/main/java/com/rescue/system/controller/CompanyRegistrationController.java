package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateCompanyRegistrationRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.CompanyRegistrationResponse;
import com.rescue.system.exception.ApiException;
import com.rescue.system.service.CompanyRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company-registrations")
public class CompanyRegistrationController {

    private final CompanyRegistrationService companyRegistrationService;

    public CompanyRegistrationController(CompanyRegistrationService companyRegistrationService) {
        this.companyRegistrationService = companyRegistrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CompanyRegistrationResponse> submit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCompanyRegistrationRequest request
    ) {
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        CompanyRegistrationResponse data = companyRegistrationService.submitRegistration(user.getUsername(), request);
        ApiResponse<CompanyRegistrationResponse> res = ApiResponse.of("Gửi đơn đăng ký công ty thành công", data);
        res.setStatus(HttpStatus.CREATED.value());
        return res;
    }

    @GetMapping("/my")
    public ApiResponse<CompanyRegistrationResponse> getMy(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        CompanyRegistrationResponse data = companyRegistrationService.getMyLatestRegistration(user.getUsername());
        return ApiResponse.of("Lấy đơn đăng ký của tôi thành công", data);
    }
}
