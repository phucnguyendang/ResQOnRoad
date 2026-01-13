package com.rescue.system.service.impl;

import com.rescue.system.dto.request.CreateRescueRequestDto;
import com.rescue.system.dto.request.RejectRescueRequestDto;
import com.rescue.system.dto.request.UpdateRescueStatusDto;
import com.rescue.system.dto.response.RescueRequestDetailDto;
import com.rescue.system.dto.response.RescueRequestDto;
import com.rescue.system.dto.response.UpdateRescueStatusResponseDto;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.RescueRequest;
import com.rescue.system.entity.RescueRequestImage;
import com.rescue.system.entity.Role;
import com.rescue.system.entity.RescueStatus;
import com.rescue.system.entity.RescueStatusHistory;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.RescueRequestRepository;
import com.rescue.system.repository.RescueRequestImageRepository;
import com.rescue.system.repository.RescueStatusHistoryRepository;
import com.rescue.system.service.RescueRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RescueRequestServiceImpl implements RescueRequestService {

    @Autowired
    private RescueRequestRepository rescueRequestRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RescueStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private RescueRequestImageRepository rescueRequestImageRepository;

    @Override
    @Transactional
    public RescueRequestDto createRescueRequest(Long userId, CreateRescueRequestDto requestDto) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        // companyId in CreateRescueRequestDto refers to rescue_companies.id.
        // Resolve that into the corresponding COMPANY account via accounts.company_id.
        Account company = accountRepository
                .findFirstByCompanyIdAndRole(requestDto.getCompanyId(), Role.COMPANY)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rescue company not found"));

        RescueRequest rescueRequest = new RescueRequest();
        rescueRequest.setUser(user);
        rescueRequest.setCompany(company);
        rescueRequest.setLocation(requestDto.getLocation());
        rescueRequest.setLatitude(requestDto.getLatitude());
        rescueRequest.setLongitude(requestDto.getLongitude());
        rescueRequest.setDescription(requestDto.getDescription());
        rescueRequest.setServiceType(requestDto.getServiceType());
        rescueRequest.setStatus(RescueStatus.PENDING_CONFIRMATION);
        rescueRequest.setCreatedAt(Instant.now());

        RescueRequest savedRequest = rescueRequestRepository.save(rescueRequest);

        // Persist incident images (base64) if provided
        if (requestDto.getImagesBase64() != null && !requestDto.getImagesBase64().isEmpty()) {
            List<RescueRequestImage> images = requestDto.getImagesBase64().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(s -> new RescueRequestImage(savedRequest, s))
                    .collect(Collectors.toList());
            if (!images.isEmpty()) {
                rescueRequestImageRepository.saveAll(images);
            }
        }

        // Create initial status history entry
        RescueStatusHistory history = new RescueStatusHistory(
                savedRequest, null, RescueStatus.PENDING_CONFIRMATION, "SYSTEM"
        );
        statusHistoryRepository.save(history);

        return new RescueRequestDto(savedRequest);
    }

    @Override
    public RescueRequestDto getRescueRequestById(Long requestId) {
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rescue request not found"));
        return new RescueRequestDto(rescueRequest);
    }

    @Override
    public RescueRequestDetailDto getRescueRequestDetailById(Long requestId) {
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rescue request not found"));

        RescueRequestDetailDto detail = new RescueRequestDetailDto();
        detail.setId(rescueRequest.getId());
        detail.setStatus(rescueRequest.getStatus() != null ? rescueRequest.getStatus().name() : null);

        // user
        if (rescueRequest.getUser() != null) {
            detail.setUser(new RescueRequestDetailDto.UserInfo(
                rescueRequest.getUser().getFullName(),
                rescueRequest.getUser().getPhoneNumber()
            ));
        }

        // incident
        RescueRequestDetailDto.IncidentInfo incident = new RescueRequestDetailDto.IncidentInfo();
        incident.setDesc(rescueRequest.getDescription());
        incident.setAddress(rescueRequest.getLocation());
        List<String> imageBase64List = rescueRequestImageRepository.findByRescueRequestIdOrderByIdAsc(requestId)
            .stream()
            .map(RescueRequestImage::getImageBase64)
            .collect(Collectors.toList());
        incident.setImagesBase64(imageBase64List);
        detail.setIncident(incident);

        // company
        if (rescueRequest.getCompany() != null) {
            detail.setCompany(new RescueRequestDetailDto.CompanyInfo(
                rescueRequest.getCompany().getCompanyId(),
                rescueRequest.getCompany().getFullName(),
                rescueRequest.getCompany().getPhoneNumber()
            ));
        }

        // timeline
        List<RescueStatusHistory> histories = statusHistoryRepository
            .findByRescueRequestIdOrderByChangedAtAsc(requestId);
        List<RescueRequestDetailDto.TimelineItem> timeline = histories.stream()
            .map(h -> new RescueRequestDetailDto.TimelineItem(
                h.getNewStatus() != null ? h.getNewStatus().name() : null,
                h.getChangedAt(),
                h.getReason() != null ? h.getReason() : ""
            ))
            .collect(Collectors.toList());
        detail.setTimeline(timeline);

        return detail;
    }

    @Override
    public List<RescueRequestDto> getRescueRequestsByUserId(Long userId) {
        List<RescueRequest> rescueRequests = rescueRequestRepository.findByUserId(userId);
        return rescueRequests.stream()
                .map(RescueRequestDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RescueRequestDto> getRescueRequestsByCompanyId(Long companyId) {
        List<RescueRequest> rescueRequests = rescueRequestRepository.findByCompanyId(companyId);
        return rescueRequests.stream()
                .map(RescueRequestDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RescueRequestDto> getRescueRequestsByStatus(RescueStatus status) {
        List<RescueRequest> rescueRequests = rescueRequestRepository.findByStatus(status);
        return rescueRequests.stream()
                .map(RescueRequestDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RescueRequestDto> getAllRescueRequests() {
        return rescueRequestRepository.findAll().stream()
                .map(RescueRequestDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RescueRequestDto acceptRescueRequest(Long requestId, Long companyId) {
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rescue request not found"));

        // Verify request is in PENDING_CONFIRMATION status
        if (rescueRequest.getStatus() != RescueStatus.PENDING_CONFIRMATION) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot accept request - current status: " + rescueRequest.getStatus().getDisplayName()
            );
        }

        Account company = accountRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Company not found"));

        // If user already selected a company at creation time, only that company can accept.
        if (rescueRequest.getCompany() != null && !rescueRequest.getCompany().getId().equals(companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "This rescue request is assigned to another company");
        }

        // Assign company and update status
        rescueRequest.setCompany(company);
        RescueStatus previousStatus = rescueRequest.getStatus();
        rescueRequest.setStatus(RescueStatus.ACCEPTED);
        rescueRequest.setUpdatedAt(Instant.now());

        RescueRequest updatedRequest = rescueRequestRepository.save(rescueRequest);

        // Record status history
        RescueStatusHistory history = new RescueStatusHistory(
                updatedRequest, previousStatus, RescueStatus.ACCEPTED, "COMPANY_" + companyId
        );
        statusHistoryRepository.save(history);

        // TODO: Send notification to user
        // notificationService.notifyUser(rescueRequest.getUser().getId(),
        //     "Yêu cầu của bạn đã được tiếp nhận bởi " + company.getFullName());

        return new RescueRequestDto(updatedRequest);
    }

    @Override
    @Transactional
    public RescueRequestDto updateRescueRequestStatus(Long requestId, Long companyId, UpdateRescueStatusDto statusDto) {
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rescue request not found"));

        // Verify company owns this request
        if (rescueRequest.getCompany() == null || !rescueRequest.getCompany().getId().equals(companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Company does not own this rescue request");
        }

        // Check if request is in a cancelled state
        if (rescueRequest.getStatus() == RescueStatus.CANCELLED_BY_USER) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu đã bị người dùng hủy — không thể cập nhật");
        }

        // Check if request is in a rejected state
        if (rescueRequest.getStatus() == RescueStatus.REJECTED_BY_COMPANY) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu đã bị từ chối — không thể cập nhật");
        }

        // Check if request is already completed
        if (rescueRequest.getStatus() == RescueStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu đã hoàn thành — không thể cập nhật");
        }

        // Parse new status
        RescueStatus newStatus;
        try {
            newStatus = RescueStatus.valueOf(statusDto.getStatus());
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status: " + statusDto.getStatus());
        }

        // Validate status transition
        if (!isValidStatusTransition(rescueRequest.getStatus(), newStatus)) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition from " + rescueRequest.getStatus() + " to " + newStatus
            );
        }

        RescueStatus previousStatus = rescueRequest.getStatus();
        rescueRequest.setStatus(newStatus);
        rescueRequest.setUpdatedAt(Instant.now());

        // If status is COMPLETED, set completedAt timestamp
        if (newStatus == RescueStatus.COMPLETED) {
            rescueRequest.setCompletedAt(Instant.now());
        }

        RescueRequest updatedRequest = rescueRequestRepository.save(rescueRequest);

        // Record status history
        RescueStatusHistory history = new RescueStatusHistory(
                updatedRequest, previousStatus, newStatus, "COMPANY_" + companyId
        );
        if (statusDto.getNote() != null) {
            history.setReason(statusDto.getNote());
        }
        statusHistoryRepository.save(history);

        // TODO: Send real-time notification to user
        // notificationService.notifyUserRealtime(rescueRequest.getUser().getId(),
        //     "Trạng thái yêu cầu cứu hộ: " + newStatus.getDisplayName());

        return new RescueRequestDto(updatedRequest);
    }

    @Override
    @Transactional
    public RescueRequestDto rejectRescueRequest(Long requestId, Long companyId, RejectRescueRequestDto rejectDto) {
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rescue request not found"));

        // Verify request is in PENDING_CONFIRMATION status (per use case, rejection happens at step 3a)
        if (rescueRequest.getStatus() != RescueStatus.PENDING_CONFIRMATION) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot reject request in current status: " + rescueRequest.getStatus().getDisplayName()
            );
        }

        Account company = accountRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Company not found"));

        // If user already selected a company at creation time, only that company can reject.
        if (rescueRequest.getCompany() != null && !rescueRequest.getCompany().getId().equals(companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "This rescue request is assigned to another company");
        }

        // Set rejection reason and update status
        rescueRequest.setCompany(company);
        rescueRequest.setRejectionReason(rejectDto.getRejectionReason());
        RescueStatus previousStatus = rescueRequest.getStatus();
        rescueRequest.setStatus(RescueStatus.REJECTED_BY_COMPANY);
        rescueRequest.setUpdatedAt(Instant.now());

        RescueRequest updatedRequest = rescueRequestRepository.save(rescueRequest);

        // Record status history
        RescueStatusHistory history = new RescueStatusHistory(
                updatedRequest, previousStatus, RescueStatus.REJECTED_BY_COMPANY, "COMPANY_" + companyId
        );
        history.setReason(rejectDto.getRejectionReason());
        statusHistoryRepository.save(history);

        // TODO: Send notification to user
        // notificationService.notifyUser(rescueRequest.getUser().getId(),
        //     "Yêu cầu cứu hộ của bạn đã bị từ chối bởi " + company.getFullName() + 
        //     ". Lý do: " + rejectDto.getRejectionReason());

        return new RescueRequestDto(updatedRequest);
    }

    @Override
    @Transactional
    public RescueRequestDto cancelRescueRequest(Long requestId, Long userId) {
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rescue request not found"));

        // Verify user owns this request
        if (!rescueRequest.getUser().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User does not own this rescue request");
        }

        // Check if request can be cancelled
        if (rescueRequest.getStatus() == RescueStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot cancel completed request");
        }

        if (rescueRequest.getStatus() == RescueStatus.CANCELLED_BY_USER) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Request is already cancelled");
        }

        RescueStatus previousStatus = rescueRequest.getStatus();
        rescueRequest.setStatus(RescueStatus.CANCELLED_BY_USER);
        rescueRequest.setUpdatedAt(Instant.now());

        RescueRequest updatedRequest = rescueRequestRepository.save(rescueRequest);

        // Record status history
        RescueStatusHistory history = new RescueStatusHistory(
                updatedRequest, previousStatus, RescueStatus.CANCELLED_BY_USER, "USER_" + userId
        );
        statusHistoryRepository.save(history);

        // TODO: Notify company if request was accepted
        // if (rescueRequest.getCompany() != null) {
        //     notificationService.notifyCompany(rescueRequest.getCompany().getId(),
        //         "Người dùng đã hủy yêu cầu cứu hộ #" + requestId);
        // }

        return new RescueRequestDto(updatedRequest);
    }

    @Override
    @Transactional
    public UpdateRescueStatusResponseDto updateRescueRequestStatusWithHistory(Long requestId, Long companyId, UpdateRescueStatusDto statusDto) {
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rescue request not found"));

        // Verify company owns this request
        if (rescueRequest.getCompany() == null || !rescueRequest.getCompany().getId().equals(companyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Company does not own this rescue request");
        }

        // Check if request is in a cancelled state
        if (rescueRequest.getStatus() == RescueStatus.CANCELLED_BY_USER) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu đã bị người dùng hủy — không thể cập nhật");
        }

        // Check if request is in a rejected state
        if (rescueRequest.getStatus() == RescueStatus.REJECTED_BY_COMPANY) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu đã bị từ chối — không thể cập nhật");
        }

        // Check if request is already completed
        if (rescueRequest.getStatus() == RescueStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu đã hoàn thành — không thể cập nhật");
        }

        // Parse new status
        RescueStatus newStatus;
        try {
            newStatus = RescueStatus.valueOf(statusDto.getStatus());
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status: " + statusDto.getStatus());
        }

        // Validate status transition
        if (!isValidStatusTransition(rescueRequest.getStatus(), newStatus)) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition from " + rescueRequest.getStatus() + " to " + newStatus
            );
        }

        RescueStatus previousStatus = rescueRequest.getStatus();
        rescueRequest.setStatus(newStatus);
        rescueRequest.setUpdatedAt(Instant.now());

        // If status is COMPLETED, set completedAt timestamp
        if (newStatus == RescueStatus.COMPLETED) {
            rescueRequest.setCompletedAt(Instant.now());
        }

        RescueRequest updatedRequest = rescueRequestRepository.save(rescueRequest);

        // Record status history
        RescueStatusHistory history = new RescueStatusHistory(
                updatedRequest, previousStatus, newStatus, "COMPANY_" + companyId
        );
        if (statusDto.getNote() != null) {
            history.setReason(statusDto.getNote());
        }
        statusHistoryRepository.save(history);

        // Get all status history for this request
        List<RescueStatusHistory> allHistory = statusHistoryRepository.findByRescueRequestIdOrderByChangedAtDesc(requestId);

        // TODO: Send real-time notification to user
        // notificationService.notifyUserRealtime(rescueRequest.getUser().getId(),
        //     "Trạng thái yêu cầu cứu hộ: " + newStatus.getDisplayName());

        return new UpdateRescueStatusResponseDto(updatedRequest, allHistory);
    }

    @Override
    public boolean canCompanyUpdateRequest(Long requestId, Long companyId) {
        RescueRequest rescueRequest = rescueRequestRepository.findById(requestId).orElse(null);
        
        if (rescueRequest == null) {
            return false;
        }

        // Company can only update if they own the request
        if (rescueRequest.getCompany() == null || !rescueRequest.getCompany().getId().equals(companyId)) {
            return false;
        }

        // Cannot update if request is cancelled, rejected, or completed
        RescueStatus status = rescueRequest.getStatus();
        return status != RescueStatus.CANCELLED_BY_USER 
                && status != RescueStatus.REJECTED_BY_COMPANY 
                && status != RescueStatus.COMPLETED;
    }

    /**
     * Validate status transitions based on use case flow
     */
    private boolean isValidStatusTransition(RescueStatus currentStatus, RescueStatus newStatus) {
        // Cannot transition to PENDING_CONFIRMATION
        if (newStatus == RescueStatus.PENDING_CONFIRMATION) {
            return false;
        }

        // Cannot transition to REJECTED_BY_COMPANY or CANCELLED_BY_USER
        // These are set via dedicated methods
        if (newStatus == RescueStatus.REJECTED_BY_COMPANY || newStatus == RescueStatus.CANCELLED_BY_USER) {
            return false;
        }

        switch (currentStatus) {
            case ACCEPTED:
                return newStatus == RescueStatus.IN_TRANSIT 
                        || newStatus == RescueStatus.IN_PROGRESS 
                        || newStatus == RescueStatus.COMPLETED;

            case IN_TRANSIT:
                return newStatus == RescueStatus.IN_PROGRESS || newStatus == RescueStatus.COMPLETED;

            case IN_PROGRESS:
                return newStatus == RescueStatus.COMPLETED;

            case COMPLETED:
            case REJECTED_BY_COMPANY:
            case CANCELLED_BY_USER:
                return false;

            default:
                return false;
        }
    }
}
