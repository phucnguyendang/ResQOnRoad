package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateCompanyAdminRequest;
import com.rescue.system.dto.request.UpdateCompanyAdminRequest;
import com.rescue.system.dto.request.UpdateCompanyStatusRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.CompanyAdminResponse;
import com.rescue.system.service.CompanyAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/companies")
@PreAuthorize("hasRole('ADMIN')")
public class CompanyAdminController {

    private final CompanyAdminService companyAdminService;

    public CompanyAdminController(CompanyAdminService companyAdminService) {
        this.companyAdminService = companyAdminService;
    }

    /**
     * UC403: Lấy danh sách tất cả công ty cứu hộ (Step 2)
     * GET /api/admin/companies?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public ApiResponse<Page<CompanyAdminResponse>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") 
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<CompanyAdminResponse> companies = companyAdminService.getAllCompanies(pageable);
        return ApiResponse.of("Lấy danh sách công ty thành công", companies);
    }

    /**
     * UC403: Xem chi tiết một công ty (Step 3-4)
     * GET /api/admin/companies/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<CompanyAdminResponse> getCompanyDetail(@PathVariable Long id) {
        CompanyAdminResponse company = companyAdminService.getCompanyDetail(id);
        return ApiResponse.of("Lấy chi tiết công ty thành công", company);
    }

    /**
     * UC403: Tạo mới công ty cứu hộ (Step 5 - Add new)
     * POST /api/admin/companies
     * 
     * Request body:
     * {
     *   "name": "Cứu hộ Ba Đình",
     *   "address": "123 Đường ABC, Ba Đình, Hà Nội",
     *   "phone": "0243123456",
     *   "email": "contact@cuuhobadih.vn",
     *   "latitude": 21.0285,
     *   "longitude": 105.8542,
     *   "serviceRadius": 50,
     *   "taxCode": "0101234567",
     *   "hotline": "0243123456",
     *   "operatingHours": "24/7",
     *   "businessLicense": "03-05-2023",
     *   "licenseDocumentUrl": "https://...",
     *   "description": "Dịch vụ cứu hộ 24/7",
     *   "username": "badinh_rescue",
     *   "password": "password123"
     * }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CompanyAdminResponse> createCompany(
            @Valid @RequestBody CreateCompanyAdminRequest request) {
        CompanyAdminResponse company = companyAdminService.createCompany(request);
        ApiResponse<CompanyAdminResponse> response = ApiResponse.of("Tạo công ty thành công", company);
        response.setStatus(HttpStatus.CREATED.value());
        return response;
    }

    /**
     * UC403: Chỉnh sửa thông tin công ty (Step 5 - Edit)
     * PUT /api/admin/companies/{id}
     * 
     * Request body (các trường có thể tùy chọn):
     * {
     *   "name": "Cứu hộ Ba Đình Pro",
     *   "address": "456 Đường XYZ, Ba Đình, Hà Nội",
     *   "phone": "0243654321",
     *   "email": "new-email@cuuhobadih.vn",
     *   "latitude": 21.0300,
     *   "longitude": 105.8550,
     *   "serviceRadius": 60,
     *   "hotline": "0243654321",
     *   "operatingHours": "6:00-22:00"
     * }
     */
    @PutMapping("/{id}")
    public ApiResponse<CompanyAdminResponse> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCompanyAdminRequest request) {
        CompanyAdminResponse company = companyAdminService.updateCompany(id, request);
        return ApiResponse.of("Cập nhật thông tin công ty thành công", company);
    }

    /**
     * UC403: Cập nhật trạng thái hoạt động của công ty (Step 5 - Lock/Delete)
     * PATCH /api/admin/companies/{id}/status
     * 
     * Request body:
     * {
     *   "status": "ACTIVE|LOCKED|DELETED",
     *   "reason": "Lý do khóa/xóa (nếu cần)"
     * }
     * 
     * Trạng thái:
     * - ACTIVE: Công ty hoạt động bình thường
     * - LOCKED: Công ty bị khóa tạm thời
     * - DELETED: Công ty bị xóa khỏi hệ thống
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<CompanyAdminResponse> updateCompanyStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCompanyStatusRequest request) {
        CompanyAdminResponse company = companyAdminService.updateCompanyStatus(id, request);
        return ApiResponse.of("Cập nhật trạng thái công ty thành công", company);
    }

    /**
     * UC403: Xóa công ty (Step 5 - Delete)
     * DELETE /api/admin/companies/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCompany(@PathVariable Long id) {
        companyAdminService.deleteCompany(id);
        return new ApiResponse<>("Xóa công ty thành công", null);
    }

    /**
     * UC403: Xác minh/Duyệt hồ sơ công ty (Step 6 - Verify License)
     * POST /api/admin/companies/{id}/verify
     * 
     * Hệ thống kiểm tra:
     * - Giấy phép kinh doanh đã được cung cấp
     * - Địa chỉ đầy đủ
     * - Mã số thuế hợp lệ
     */
    @PostMapping("/{id}/verify")
    public ApiResponse<CompanyAdminResponse> verifyCompany(@PathVariable Long id) {
        CompanyAdminResponse company = companyAdminService.verifyCompany(id);
        return ApiResponse.of("Xác minh công ty thành công", company);
    }

    /**
     * UC403: Từ chối duyệt hồ sơ công ty (Step 6a - Reject License)
     * POST /api/admin/companies/{id}/reject
     * 
     * Request body:
     * {
     *   "reason": "Giấy phép không hợp lệ"
     * }
     */
    @PostMapping("/{id}/reject")
    public ApiResponse<CompanyAdminResponse> rejectCompanyVerification(
            @PathVariable Long id,
            @RequestParam String reason) {
        CompanyAdminResponse company = companyAdminService.rejectCompanyVerification(id, reason);
        return ApiResponse.of("Từ chối duyệt hồ sơ thành công", company);
    }
}
