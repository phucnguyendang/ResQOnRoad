package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateServiceRequest;
import com.rescue.system.dto.request.UpdateServiceRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.ServiceDetailResponse;
import com.rescue.system.exception.ApiException;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for rescue service management
 * UC302 - Quản lý dịch vụ cứu hộ
 * 
 * APIs:
 * - GET /api/services/company/{companyId} - Get all services for a company
 * - GET /api/services/{serviceId} - Get service details
 * - POST /api/services - Create new service (Admin/Company)
 * - PUT /api/services/{serviceId} - Update service (Admin/Company)
 * - DELETE /api/services/{serviceId} - Delete service (Admin/Company)
 * - GET /api/services/company/{companyId}/available - Get available services
 */
@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * UC302 - Step 2: Get all services for a company
     * GET /api/services/company/my
     * 
     * Hiển thị thông tin các dịch vụ hiện tại của công ty:
     * - Danh mục các dịch vụ: vá lốp, thay lốp, nạp nhiên liệu, kéo xe, sửa chữa
     * - Chi phí cho từng dịch vụ
     * - Trạng thái dịch vụ (available/unavailable)
     * - Thời gian ước tính
     */
    @GetMapping("/company/my")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<List<ServiceDetailResponse>>> getMyCompanyServices(
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getAccountIdFromToken(token);
            List<ServiceDetailResponse> services = serviceService.getServicesByCompanyId(companyId);

            ApiResponse<List<ServiceDetailResponse>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách dịch vụ thành công",
                    services);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách dịch vụ: " + e.getMessage());
        }
    }

    /**
     * Get all services for a company by company ID (public endpoint)
     * GET /api/services/company/{companyId}
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<ServiceDetailResponse>>> getServicesByCompanyId(
            @PathVariable Long companyId) {
        try {
            List<ServiceDetailResponse> services = serviceService.getServicesByCompanyId(companyId);

            ApiResponse<List<ServiceDetailResponse>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách dịch vụ thành công",
                    services);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách dịch vụ: " + e.getMessage());
        }
    }

    /**
     * Get service details by ID
     * GET /api/services/{serviceId}
     */
    @GetMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> getServiceById(
            @PathVariable Long serviceId) {
        try {
            ServiceDetailResponse service = serviceService.getServiceById(serviceId);

            ApiResponse<ServiceDetailResponse> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy chi tiết dịch vụ thành công",
                    service);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy chi tiết dịch vụ: " + e.getMessage());
        }
    }

    /**
     * UC302 - Step 3: Create new service
     * POST /api/services
     * 
     * Quản trị viên/Công ty cứu hộ tạo mới dịch vụ:
     * - Tên dịch vụ
     * - Loại dịch vụ (Vá lốp, thay lốp, nạp nhiên liệu, kéo xe, sửa chữa)
     * - Giá dịch vụ
     * - Trạng thái (available/unavailable)
     * - Thời gian ước tính
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> createService(
            @Valid @RequestBody CreateServiceRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long accountId = getAccountIdFromToken(token);
            ServiceDetailResponse service = serviceService.createService(accountId, request);

            ApiResponse<ServiceDetailResponse> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Tạo dịch vụ thành công",
                    service);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi tạo dịch vụ: " + e.getMessage());
        }
    }

    /**
     * UC302 - Step 3, 4, 5: Update service
     * PUT /api/services/{serviceId}
     * 
     * Quản trị viên/Công ty cứu hộ cập nhật thông tin dịch vụ:
     * - Hệ thống hiển thị cửa sổ xác nhận
     * - Quản trị viên xác nhận thay đổi
     * - Hệ thống hiển thị thông tin sau thay đổi
     */
    @PutMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody UpdateServiceRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long accountId = getAccountIdFromToken(token);
            ServiceDetailResponse service = serviceService.updateService(serviceId, accountId, request);

            ApiResponse<ServiceDetailResponse> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Cập nhật dịch vụ thành công",
                    service);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi cập nhật dịch vụ: " + e.getMessage());
        }
    }

    /**
     * Delete service
     * DELETE /api/services/{serviceId}
     */
    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<Void>> deleteService(
            @PathVariable Long serviceId,
            @RequestHeader("Authorization") String token) {
        try {
            Long accountId = getAccountIdFromToken(token);
            serviceService.deleteService(serviceId, accountId);

            ApiResponse<Void> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Xóa dịch vụ thành công",
                    null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi xóa dịch vụ: " + e.getMessage());
        }
    }

    /**
     * Get available services for a company
     * GET /api/services/company/{companyId}/available
     * 
     * Lấy danh sách các dịch vụ khả dụng của công ty
     */
    @GetMapping("/company/{companyId}/available")
    public ResponseEntity<ApiResponse<List<ServiceDetailResponse>>> getAvailableServicesByCompanyId(
            @PathVariable Long companyId) {
        try {
            List<ServiceDetailResponse> services = serviceService.getAvailableServicesByCompanyId(companyId);

            ApiResponse<List<ServiceDetailResponse>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách dịch vụ khả dụng thành công",
                    services);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách dịch vụ khả dụng: " + e.getMessage());
        }
    }

    /**
     * Helper method to extract account ID from JWT token
     */
    private Long getAccountIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            return jwtTokenProvider.getUserIdFromToken(jwt);
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
    }
}
