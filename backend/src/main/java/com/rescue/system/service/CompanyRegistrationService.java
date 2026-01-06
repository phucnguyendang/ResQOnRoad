package com.rescue.system.service;

import com.rescue.system.dto.request.CreateCompanyRegistrationRequest;
import com.rescue.system.dto.response.CompanyRegistrationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyRegistrationService {

    CompanyRegistrationResponse submitRegistration(String username, CreateCompanyRegistrationRequest request);

    CompanyRegistrationResponse getMyLatestRegistration(String username);

    Page<CompanyRegistrationResponse> adminListRegistrations(String adminUsername, String status, Pageable pageable);

    CompanyRegistrationResponse adminApprove(String adminUsername, Long registrationId);

    CompanyRegistrationResponse adminReject(String adminUsername, Long registrationId, String reason);
}
