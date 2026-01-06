package com.rescue.system.controller;

import com.rescue.system.dto.request.AddWorkflowStepRequest;
import com.rescue.system.dto.request.UpdateWorkflowStatusRequest;
import com.rescue.system.dto.request.UpdateWorkflowStepRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.WorkflowDto;
import com.rescue.system.dto.response.WorkflowStepDto;
import com.rescue.system.entity.WorkflowStatus;
import com.rescue.system.exception.ApiException;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho UC304 - Quản lý quy trình cứu hộ
 * 
 * APIs:
 * - GET /api/workflows - Lấy tất cả quy trình (Admin)
 * - GET /api/workflows/active - Lấy quy trình đang hoạt động (Admin)
 * - GET /api/workflows/status/{status} - Lấy quy trình theo trạng thái (Admin)
 * - GET /api/workflows/{id} - Lấy chi tiết quy trình
 * - GET /api/workflows/request/{requestId} - Lấy quy trình theo yêu cầu cứu hộ
 * - GET /api/workflows/company/my-workflows - Lấy quy trình của công ty
 * - GET /api/workflows/company/active - Lấy quy trình đang hoạt động của công
 * ty
 * - GET /api/workflows/user/my-workflows - Lấy quy trình của user
 * - POST /api/workflows - Tạo quy trình mới (Company)
 * - PATCH /api/workflows/{id}/status - Cập nhật trạng thái quy trình (Company)
 * - POST /api/workflows/{id}/steps - Thêm bước xử lý (Company)
 * - PATCH /api/workflows/steps/{stepId} - Cập nhật bước xử lý (Company)
 * - GET /api/workflows/{id}/steps - Lấy danh sách bước xử lý
 * - POST /api/workflows/{id}/complete - Hoàn thành quy trình (Company)
 * - POST /api/workflows/{id}/cancel - Hủy quy trình (Company)
 * 
 * Admin APIs:
 * - GET /api/admin/workflows - Lấy tất cả quy trình hệ thống
 * - PATCH /api/admin/workflows/{id}/status - Cập nhật trạng thái quy trình
 * (Admin)
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ==================== ADMIN APIs ====================

    /**
     * GET /api/admin/workflows - Lấy tất cả quy trình (Admin)
     */
    @GetMapping("/admin/workflows")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkflowDto>>> getAllWorkflows() {
        try {
            List<WorkflowDto> workflows = workflowService.getAllWorkflows();
            return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách quy trình thành công", workflows));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách quy trình: " + e.getMessage());
        }
    }

    /**
     * GET /api/admin/workflows/active - Lấy quy trình đang hoạt động (Admin)
     */
    @GetMapping("/admin/workflows/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkflowDto>>> getActiveWorkflows() {
        try {
            List<WorkflowDto> workflows = workflowService.getActiveWorkflows();
            return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách quy trình đang hoạt động thành công", workflows));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách quy trình: " + e.getMessage());
        }
    }

    /**
     * GET /api/admin/workflows/status/{status} - Lấy quy trình theo trạng thái
     * (Admin)
     */
    @GetMapping("/admin/workflows/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkflowDto>>> getWorkflowsByStatus(@PathVariable String status) {
        try {
            WorkflowStatus workflowStatus = WorkflowStatus.valueOf(status.toUpperCase());
            List<WorkflowDto> workflows = workflowService.getWorkflowsByStatus(workflowStatus);
            return ResponseEntity
                    .ok(new ApiResponse<>("Lấy danh sách quy trình theo trạng thái thành công", workflows));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ: " + status);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách quy trình: " + e.getMessage());
        }
    }

    /**
     * PATCH /api/admin/workflows/{id}/status - Cập nhật trạng thái quy trình
     * (Admin)
     */
    @PatchMapping("/admin/workflows/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkflowDto>> updateWorkflowStatusByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkflowStatusRequest request) {
        try {
            WorkflowDto workflow = workflowService.updateWorkflowStatusByAdmin(id, request);
            return ResponseEntity.ok(new ApiResponse<>("Cập nhật trạng thái quy trình thành công", workflow));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
    }

    // ==================== COMMON APIs ====================

    /**
     * GET /api/workflows/{id} - Lấy chi tiết quy trình
     */
    @GetMapping("/workflows/{id}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<WorkflowDto>> getWorkflowById(@PathVariable Long id) {
        try {
            WorkflowDto workflow = workflowService.getWorkflowById(id);
            return ResponseEntity.ok(new ApiResponse<>("Lấy thông tin quy trình thành công", workflow));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy thông tin quy trình: " + e.getMessage());
        }
    }

    /**
     * GET /api/workflows/request/{requestId} - Lấy quy trình theo yêu cầu cứu hộ
     */
    @GetMapping("/workflows/request/{requestId}")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<WorkflowDto>> getWorkflowByRescueRequestId(@PathVariable Long requestId) {
        try {
            WorkflowDto workflow = workflowService.getWorkflowByRescueRequestId(requestId);
            return ResponseEntity.ok(new ApiResponse<>("Lấy thông tin quy trình thành công", workflow));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy thông tin quy trình: " + e.getMessage());
        }
    }

    /**
     * GET /api/workflows/{id}/steps - Lấy danh sách bước xử lý
     */
    @GetMapping("/workflows/{id}/steps")
    @PreAuthorize("hasAnyRole('USER', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkflowStepDto>>> getWorkflowSteps(@PathVariable Long id) {
        try {
            List<WorkflowStepDto> steps = workflowService.getWorkflowSteps(id);
            return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách bước xử lý thành công", steps));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách bước xử lý: " + e.getMessage());
        }
    }

    // ==================== COMPANY APIs ====================

    /**
     * GET /api/workflows/company/my-workflows - Lấy tất cả quy trình của công ty
     */
    @GetMapping("/workflows/company/my-workflows")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<List<WorkflowDto>>> getCompanyWorkflows(
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            List<WorkflowDto> workflows = workflowService.getWorkflowsByCompanyId(companyId);
            return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách quy trình của công ty thành công", workflows));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách quy trình: " + e.getMessage());
        }
    }

    /**
     * GET /api/workflows/company/active - Lấy quy trình đang hoạt động của công ty
     */
    @GetMapping("/workflows/company/active")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<List<WorkflowDto>>> getActiveCompanyWorkflows(
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            List<WorkflowDto> workflows = workflowService.getActiveWorkflowsByCompanyId(companyId);
            return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách quy trình đang hoạt động thành công", workflows));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách quy trình: " + e.getMessage());
        }
    }

    /**
     * POST /api/workflows - Tạo quy trình mới (Company)
     */
    @PostMapping("/workflows")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<WorkflowDto>> createWorkflow(
            @RequestParam Long rescueRequestId,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            WorkflowDto workflow = workflowService.createWorkflow(rescueRequestId, companyId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Tạo quy trình thành công", workflow));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi tạo quy trình: " + e.getMessage());
        }
    }

    /**
     * PATCH /api/workflows/{id}/status - Cập nhật trạng thái quy trình (Company)
     */
    @PatchMapping("/workflows/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<WorkflowDto>> updateWorkflowStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkflowStatusRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            WorkflowDto workflow = workflowService.updateWorkflowStatus(id, companyId, request);
            return ResponseEntity.ok(new ApiResponse<>("Cập nhật trạng thái quy trình thành công", workflow));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
    }

    /**
     * POST /api/workflows/{id}/steps - Thêm bước xử lý (Company)
     */
    @PostMapping("/workflows/{id}/steps")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<WorkflowStepDto>> addWorkflowStep(
            @PathVariable Long id,
            @Valid @RequestBody AddWorkflowStepRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            WorkflowStepDto step = workflowService.addWorkflowStep(id, companyId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Thêm bước xử lý thành công", step));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi thêm bước xử lý: " + e.getMessage());
        }
    }

    /**
     * PATCH /api/workflows/steps/{stepId} - Cập nhật bước xử lý (Company)
     */
    @PatchMapping("/workflows/steps/{stepId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<WorkflowStepDto>> updateWorkflowStep(
            @PathVariable Long stepId,
            @Valid @RequestBody UpdateWorkflowStepRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            WorkflowStepDto step = workflowService.updateWorkflowStep(stepId, companyId, request);
            return ResponseEntity.ok(new ApiResponse<>("Cập nhật bước xử lý thành công", step));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật bước xử lý: " + e.getMessage());
        }
    }

    /**
     * POST /api/workflows/{id}/complete - Hoàn thành quy trình (Company)
     */
    @PostMapping("/workflows/{id}/complete")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<WorkflowDto>> completeWorkflow(
            @PathVariable Long id,
            @RequestParam(required = false) String completionNotes,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            WorkflowDto workflow = workflowService.completeWorkflow(id, companyId, completionNotes);
            return ResponseEntity.ok(new ApiResponse<>("Hoàn thành quy trình thành công", workflow));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi hoàn thành quy trình: " + e.getMessage());
        }
    }

    /**
     * POST /api/workflows/{id}/cancel - Hủy quy trình (Company)
     */
    @PostMapping("/workflows/{id}/cancel")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<WorkflowDto>> cancelWorkflow(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestHeader("Authorization") String token) {
        try {
            Long companyId = getUserIdFromToken(token);
            WorkflowDto workflow = workflowService.cancelWorkflow(id, companyId, reason);
            return ResponseEntity.ok(new ApiResponse<>("Hủy quy trình thành công", workflow));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi hủy quy trình: " + e.getMessage());
        }
    }

    // ==================== USER APIs ====================

    /**
     * GET /api/workflows/user/my-workflows - Lấy quy trình của user
     */
    @GetMapping("/workflows/user/my-workflows")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<WorkflowDto>>> getUserWorkflows(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            List<WorkflowDto> workflows = workflowService.getWorkflowsByUserId(userId);
            return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách quy trình thành công", workflows));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi khi lấy danh sách quy trình: " + e.getMessage());
        }
    }

    // ==================== Helper Methods ====================

    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
        }

        String jwtToken = token.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);

        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Không thể xác thực người dùng");
        }

        return userId;
    }
}
