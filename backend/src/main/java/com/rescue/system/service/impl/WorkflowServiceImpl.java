package com.rescue.system.service.impl;

import com.rescue.system.dto.request.AddWorkflowStepRequest;
import com.rescue.system.dto.request.UpdateWorkflowStatusRequest;
import com.rescue.system.dto.request.UpdateWorkflowStepRequest;
import com.rescue.system.dto.response.WorkflowDto;
import com.rescue.system.dto.response.WorkflowStepDto;
import com.rescue.system.entity.*;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.*;
import com.rescue.system.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation của WorkflowService cho UC304 - Quản lý quy trình cứu hộ
 */
@Service
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired
    private RescueWorkflowRepository workflowRepository;

    @Autowired
    private WorkflowStepRepository stepRepository;

    @Autowired
    private RescueRequestRepository rescueRequestRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public WorkflowDto getWorkflowById(Long workflowId) {
        RescueWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(
                        () -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy quy trình với ID: " + workflowId));

        WorkflowDto dto = new WorkflowDto(workflow);
        dto.setSteps(getWorkflowSteps(workflowId));
        return dto;
    }

    @Override
    public WorkflowDto getWorkflowByRescueRequestId(Long rescueRequestId) {
        RescueWorkflow workflow = workflowRepository.findByRescueRequestId(rescueRequestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy quy trình cho yêu cầu cứu hộ: " + rescueRequestId));

        WorkflowDto dto = new WorkflowDto(workflow);
        dto.setSteps(getWorkflowSteps(workflow.getId()));
        return dto;
    }

    @Override
    public List<WorkflowDto> getAllWorkflows() {
        return workflowRepository.findAll().stream()
                .map(workflow -> {
                    WorkflowDto dto = new WorkflowDto(workflow);
                    dto.setSteps(getWorkflowSteps(workflow.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkflowDto> getActiveWorkflows() {
        return workflowRepository.findAllActiveWorkflows().stream()
                .map(workflow -> {
                    WorkflowDto dto = new WorkflowDto(workflow);
                    dto.setSteps(getWorkflowSteps(workflow.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkflowDto> getWorkflowsByStatus(WorkflowStatus status) {
        return workflowRepository.findByWorkflowStatus(status).stream()
                .map(workflow -> {
                    WorkflowDto dto = new WorkflowDto(workflow);
                    dto.setSteps(getWorkflowSteps(workflow.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkflowDto> getWorkflowsByCompanyId(Long companyId) {
        return workflowRepository.findByCompanyId(companyId).stream()
                .map(workflow -> {
                    WorkflowDto dto = new WorkflowDto(workflow);
                    dto.setSteps(getWorkflowSteps(workflow.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkflowDto> getActiveWorkflowsByCompanyId(Long companyId) {
        return workflowRepository.findActiveWorkflowsByCompanyId(companyId).stream()
                .map(workflow -> {
                    WorkflowDto dto = new WorkflowDto(workflow);
                    dto.setSteps(getWorkflowSteps(workflow.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkflowDto> getWorkflowsByUserId(Long userId) {
        return workflowRepository.findByUserId(userId).stream()
                .map(workflow -> {
                    WorkflowDto dto = new WorkflowDto(workflow);
                    dto.setSteps(getWorkflowSteps(workflow.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public WorkflowDto createWorkflow(Long rescueRequestId, Long companyId) {
        // Kiểm tra xem đã có workflow cho request này chưa
        if (workflowRepository.existsByRescueRequestId(rescueRequestId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Quy trình đã tồn tại cho yêu cầu cứu hộ này");
        }

        RescueRequest rescueRequest = rescueRequestRepository.findById(rescueRequestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy yêu cầu cứu hộ: " + rescueRequestId));

        Account company = accountRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy công ty: " + companyId));

        // Tạo workflow mới
        RescueWorkflow workflow = new RescueWorkflow(rescueRequest);
        workflow.setCompany(company);
        workflow.setWorkflowStatus(WorkflowStatus.ASSIGNED);
        workflow.setCurrentStep("Đã phân công");
        workflow.setStepNumber(1);
        workflow.setStartedAt(Instant.now());

        workflow = workflowRepository.save(workflow);

        // Tạo các bước xử lý mặc định
        createDefaultWorkflowSteps(workflow);

        WorkflowDto dto = new WorkflowDto(workflow);
        dto.setSteps(getWorkflowSteps(workflow.getId()));
        return dto;
    }

    /**
     * Tạo các bước xử lý mặc định cho quy trình cứu hộ
     */
    private void createDefaultWorkflowSteps(RescueWorkflow workflow) {
        String[][] defaultSteps = {
                { "Tiếp nhận yêu cầu", "Xác nhận và tiếp nhận thông tin yêu cầu cứu hộ từ khách hàng" },
                { "Điều phối xe cứu hộ", "Phân công và điều phối xe cứu hộ đến hiện trường" },
                { "Di chuyển đến hiện trường", "Xe cứu hộ đang trên đường đến vị trí khách hàng" },
                { "Đến hiện trường", "Đã đến nơi và xác nhận tình trạng xe" },
                { "Thực hiện cứu hộ", "Tiến hành công tác cứu hộ/sửa chữa" },
                { "Hoàn thành", "Hoàn tất công tác cứu hộ và bàn giao cho khách hàng" }
        };

        for (int i = 0; i < defaultSteps.length; i++) {
            WorkflowStep step = new WorkflowStep(workflow, i + 1, defaultSteps[i][0], defaultSteps[i][1]);

            // Bước đầu tiên đánh dấu là đang thực hiện
            if (i == 0) {
                step.setStepStatus(StepStatus.IN_PROGRESS);
                step.setStartedAt(Instant.now());
            }

            stepRepository.save(step);
        }
    }

    @Override
    public WorkflowDto updateWorkflowStatus(Long workflowId, Long companyId, UpdateWorkflowStatusRequest request) {
        RescueWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy quy trình: " + workflowId));

        // Kiểm tra quyền
        if (!canCompanyUpdateWorkflow(workflowId, companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật quy trình này");
        }

        return updateWorkflowStatusInternal(workflow, request);
    }

    @Override
    public WorkflowDto updateWorkflowStatusByAdmin(Long workflowId, UpdateWorkflowStatusRequest request) {
        RescueWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy quy trình: " + workflowId));

        return updateWorkflowStatusInternal(workflow, request);
    }

    private WorkflowDto updateWorkflowStatusInternal(RescueWorkflow workflow, UpdateWorkflowStatusRequest request) {
        workflow.setWorkflowStatus(request.getWorkflowStatus());
        workflow.setUpdatedAt(Instant.now());

        if (request.getNotes() != null) {
            workflow.setNotes(request.getNotes());
        }

        if (request.getEstimatedArrivalTime() != null) {
            workflow.setEstimatedArrivalTime(request.getEstimatedArrivalTime());
        }

        // Cập nhật current step dựa trên trạng thái
        updateCurrentStep(workflow, request.getWorkflowStatus());

        // Nếu trạng thái là ON_SITE, cập nhật actual arrival time
        if (request.getWorkflowStatus() == WorkflowStatus.ON_SITE) {
            workflow.setActualArrivalTime(Instant.now());
        }

        // Nếu trạng thái là COMPLETED
        if (request.getWorkflowStatus() == WorkflowStatus.COMPLETED) {
            workflow.setCompletedAt(Instant.now());
        }

        workflow = workflowRepository.save(workflow);

        WorkflowDto dto = new WorkflowDto(workflow);
        dto.setSteps(getWorkflowSteps(workflow.getId()));
        return dto;
    }

    private void updateCurrentStep(RescueWorkflow workflow, WorkflowStatus status) {
        switch (status) {
            case PENDING:
                workflow.setCurrentStep("Đang chờ xử lý");
                workflow.setStepNumber(0);
                break;
            case ASSIGNED:
                workflow.setCurrentStep("Đã phân công");
                workflow.setStepNumber(1);
                break;
            case DISPATCHED:
                workflow.setCurrentStep("Đã điều phối");
                workflow.setStepNumber(2);
                break;
            case IN_TRANSIT:
                workflow.setCurrentStep("Đang di chuyển đến hiện trường");
                workflow.setStepNumber(3);
                break;
            case ON_SITE:
                workflow.setCurrentStep("Đã đến hiện trường");
                workflow.setStepNumber(4);
                break;
            case IN_PROGRESS:
                workflow.setCurrentStep("Đang thực hiện cứu hộ");
                workflow.setStepNumber(5);
                break;
            case COMPLETED:
                workflow.setCurrentStep("Hoàn thành");
                workflow.setStepNumber(6);
                break;
            case CANCELLED:
                workflow.setCurrentStep("Đã hủy");
                break;
            case FAILED:
                workflow.setCurrentStep("Thất bại");
                break;
        }
    }

    @Override
    public WorkflowStepDto addWorkflowStep(Long workflowId, Long companyId, AddWorkflowStepRequest request) {
        RescueWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy quy trình: " + workflowId));

        if (!canCompanyUpdateWorkflow(workflowId, companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật quy trình này");
        }

        // Lấy số thứ tự bước tiếp theo
        Integer maxStepNumber = stepRepository.findMaxStepNumberByWorkflowId(workflowId).orElse(0);

        WorkflowStep step = new WorkflowStep(workflow, maxStepNumber + 1, request.getStepName(),
                request.getStepDescription());
        step.setPerformedBy(request.getPerformedBy());

        step = stepRepository.save(step);

        return new WorkflowStepDto(step);
    }

    @Override
    public WorkflowStepDto updateWorkflowStep(Long stepId, Long companyId, UpdateWorkflowStepRequest request) {
        WorkflowStep step = stepRepository.findById(stepId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy bước xử lý: " + stepId));

        if (!canCompanyUpdateWorkflow(step.getWorkflow().getId(), companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật bước xử lý này");
        }

        step.setStepStatus(request.getStepStatus());

        if (request.getNotes() != null) {
            step.setNotes(request.getNotes());
        }

        if (request.getPerformedBy() != null) {
            step.setPerformedBy(request.getPerformedBy());
        }

        // Cập nhật thời gian bắt đầu/kết thúc
        if (request.getStepStatus() == StepStatus.IN_PROGRESS && step.getStartedAt() == null) {
            step.setStartedAt(Instant.now());
        }

        if (request.getStepStatus() == StepStatus.COMPLETED || request.getStepStatus() == StepStatus.FAILED) {
            step.setCompletedAt(Instant.now());
        }

        step = stepRepository.save(step);

        return new WorkflowStepDto(step);
    }

    @Override
    public List<WorkflowStepDto> getWorkflowSteps(Long workflowId) {
        return stepRepository.findByWorkflowIdOrderByStepNumberAsc(workflowId).stream()
                .map(WorkflowStepDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public WorkflowDto completeWorkflow(Long workflowId, Long companyId, String completionNotes) {
        RescueWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy quy trình: " + workflowId));

        if (!canCompanyUpdateWorkflow(workflowId, companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật quy trình này");
        }

        workflow.setWorkflowStatus(WorkflowStatus.COMPLETED);
        workflow.setCurrentStep("Hoàn thành");
        workflow.setCompletionNotes(completionNotes);
        workflow.setCompletedAt(Instant.now());
        workflow.setUpdatedAt(Instant.now());

        // Cập nhật tất cả các bước còn lại thành completed
        List<WorkflowStep> pendingSteps = stepRepository.findNextPendingSteps(workflowId);
        for (WorkflowStep step : pendingSteps) {
            step.setStepStatus(StepStatus.COMPLETED);
            step.setCompletedAt(Instant.now());
            stepRepository.save(step);
        }

        workflow = workflowRepository.save(workflow);

        WorkflowDto dto = new WorkflowDto(workflow);
        dto.setSteps(getWorkflowSteps(workflow.getId()));
        return dto;
    }

    @Override
    public WorkflowDto cancelWorkflow(Long workflowId, Long companyId, String reason) {
        RescueWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy quy trình: " + workflowId));

        if (!canCompanyUpdateWorkflow(workflowId, companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền hủy quy trình này");
        }

        workflow.setWorkflowStatus(WorkflowStatus.CANCELLED);
        workflow.setCurrentStep("Đã hủy");
        workflow.setNotes(reason);
        workflow.setUpdatedAt(Instant.now());

        workflow = workflowRepository.save(workflow);

        WorkflowDto dto = new WorkflowDto(workflow);
        dto.setSteps(getWorkflowSteps(workflow.getId()));
        return dto;
    }

    @Override
    public boolean canCompanyUpdateWorkflow(Long workflowId, Long companyId) {
        return workflowRepository.findById(workflowId)
                .map(workflow -> workflow.getCompany() != null && workflow.getCompany().getId().equals(companyId))
                .orElse(false);
    }
}
