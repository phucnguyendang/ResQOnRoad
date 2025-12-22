package com.rescue.system.service;

import com.rescue.system.dto.request.CompanySearchRequest;
import com.rescue.system.dto.response.CompanyDetailResponse;
import com.rescue.system.dto.response.CompanySearchResponse;
import org.springframework.data.domain.Page;

public interface CompanySearchService {

    /**
     * Tìm kiếm công ty cứu hộ gần vị trí người dùng
     */
    Page<CompanySearchResponse> searchNearbyCompanies(CompanySearchRequest request);

    /**
     * Lấy thông tin chi tiết công ty
     */
    CompanyDetailResponse getCompanyDetails(Long companyId);
}
