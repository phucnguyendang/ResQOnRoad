package com.rescue.system.service;

import com.rescue.system.dto.request.AddWorkflowStepRequest;
import com.rescue.system.dto.request.UpdateWorkflowStatusRequest;
import com.rescue.system.dto.request.UpdateWorkflowStepRequest;
import com.rescue.system.dto.response.WorkflowDto;
import com.rescue.system.dto.response.WorkflowStepDto;
import com.rescue.system.entity.WorkflowStatus;

import java.util.List;

/**
 * Service interface cho quản lý quy trình cứu hộ (UC304)
 */
public interface WorkflowService {

    /**
     * Lấy thông tin quy trình theo ID
     */
    WorkflowDto getWorkflowById(Long workflowId);

    /**
     * Lấy thông tin quy trình theo rescue request ID
     */
    WorkflowDto getWorkflowByRescueRequestId(Long rescueRequestId);

    /**
     * Lấy tất cả quy trình (dành cho Admin)
     */
    List<WorkflowDto> getAllWorkflows();

    /**
     * Lấy tất cả quy trình đang hoạt động (dành cho Admin)
     */
    List<WorkflowDto> getActiveWorkflows();

    /**
     * Lấy tất cả quy trình theo trạng thái (dành cho Admin)
     */
    List<WorkflowDto> getWorkflowsByStatus(WorkflowStatus status);

    /**
     * Lấy tất cả quy trình của một công ty cứu hộ
     */
    List<WorkflowDto> getWorkflowsByCompanyId(Long companyId);

    /**
     * Lấy tất cả quy trình đang hoạt động của một công ty
     */
    List<WorkflowDto> getActiveWorkflowsByCompanyId(Long companyId);

    /**
     * Lấy tất cả quy trình của một user
     */
    List<WorkflowDto> getWorkflowsByUserId(Long userId);

    /**
     * Tạo quy trình mới khi yêu cầu cứu hộ được chấp nhận
     */
    WorkflowDto createWorkflow(Long rescueRequestId, Long companyId);

    /**
     * Cập nhật trạng thái quy trình (dành cho công ty cứu hộ)
     */
    WorkflowDto updateWorkflowStatus(Long workflowId, Long companyId, UpdateWorkflowStatusRequest request);

    /**
     * Cập nhật trạng thái quy trình bởi Admin
     */
    WorkflowDto updateWorkflowStatusByAdmin(Long workflowId, UpdateWorkflowStatusRequest request);

    /**
     * Thêm bước xử lý mới vào quy trình
     */
    WorkflowStepDto addWorkflowStep(Long workflowId, Long companyId, AddWorkflowStepRequest request);

    /**
     * Cập nhật trạng thái bước xử lý
     */
    WorkflowStepDto updateWorkflowStep(Long stepId, Long companyId, UpdateWorkflowStepRequest request);

    /**
     * Lấy danh sách các bước xử lý của quy trình
     */
    List<WorkflowStepDto> getWorkflowSteps(Long workflowId);

    /**
     * Hoàn thành quy trình
     */
    WorkflowDto completeWorkflow(Long workflowId, Long companyId, String completionNotes);

    /**
     * Hủy quy trình
     */
    WorkflowDto cancelWorkflow(Long workflowId, Long companyId, String reason);

    /**
     * Kiểm tra công ty có quyền cập nhật quy trình hay không
     */
    boolean canCompanyUpdateWorkflow(Long workflowId, Long companyId);
}
