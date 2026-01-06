package com.rescue.system.service;

import com.rescue.system.dto.request.CreateCompanyAdminRequest;
import com.rescue.system.dto.request.UpdateCompanyAdminRequest;
import com.rescue.system.dto.request.UpdateCompanyStatusRequest;
import com.rescue.system.dto.response.CompanyAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyAdminService {

    /**
     * Lấy danh sách tất cả công ty cứu hộ (có phân trang)
     */
    Page<CompanyAdminResponse> getAllCompanies(Pageable pageable);

    /**
     * Lấy chi tiết một công ty
     */
    CompanyAdminResponse getCompanyDetail(Long companyId);

    /**
     * Tạo mới công ty và tài khoản liên kết
     */
    CompanyAdminResponse createCompany(CreateCompanyAdminRequest request);

    /**
     * Cập nhật thông tin công ty
     */
    CompanyAdminResponse updateCompany(Long companyId, UpdateCompanyAdminRequest request);

    /**
     * Cập nhật trạng thái hoạt động của công ty (ACTIVE, LOCKED, DELETED)
     */
    CompanyAdminResponse updateCompanyStatus(Long companyId, UpdateCompanyStatusRequest request);

    /**
     * Xóa công ty
     */
    void deleteCompany(Long companyId);

    /**
     * Xác minh/kiểm duyệt giấy phép của công ty
     */
    CompanyAdminResponse verifyCompany(Long companyId);

    /**
     * Từ chối giấy phép của công ty
     */
    CompanyAdminResponse rejectCompanyVerification(Long companyId, String reason);
}
