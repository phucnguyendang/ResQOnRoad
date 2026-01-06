package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateVehicleRequestDto;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.VehicleResponse;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.Role;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.RescueCompanyRepository;
import com.rescue.system.service.VehicleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final AccountRepository accountRepository;
    private final RescueCompanyRepository rescueCompanyRepository;

    public VehicleController(
            VehicleService vehicleService,
            AccountRepository accountRepository,
            RescueCompanyRepository rescueCompanyRepository
    ) {
        this.vehicleService = vehicleService;
        this.accountRepository = accountRepository;
        this.rescueCompanyRepository = rescueCompanyRepository;
    }

    @GetMapping
    public ApiResponse<List<VehicleResponse>> getVehicles(
            @AuthenticationPrincipal User user
    ) {
        RescueCompany company = getAuthenticatedCompany(user);

        return ApiResponse.of(
                "Danh sách phương tiện",
                vehicleService.getCompanyVehicles(company.getId())
        );
    }

    @PostMapping
    public ApiResponse<VehicleResponse> createVehicle(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateVehicleRequestDto request
    ) {
        RescueCompany company = getAuthenticatedCompany(user);

        return ApiResponse.of(
                "Thêm phương tiện thành công",
                vehicleService.createVehicle(company, request)
        );
    }

    /**
     * Helper method:
     * - Validate authentication
     * - Load Account
     * - Ensure role = COMPANY
     * - Load RescueCompany entity
     */
    private RescueCompany getAuthenticatedCompany(User user) {
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Account account = accountRepository.findByUsername(user.getUsername())
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "Account not found"));

        if (account.getRole() != Role.COMPANY) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account is not a company");
        }

        Long companyId = account.getCompanyId();
        if (companyId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Company not linked to account");
        }

        return rescueCompanyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "Rescue company not found"));
    }
}
