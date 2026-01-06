package com.rescue.system.repository;

import com.rescue.system.entity.RescueWorkflow;
import com.rescue.system.entity.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueWorkflowRepository extends JpaRepository<RescueWorkflow, Long> {

    /**
     * Tìm workflow theo rescue request ID
     */
    Optional<RescueWorkflow> findByRescueRequestId(Long rescueRequestId);

    /**
     * Tìm tất cả workflows theo company ID
     */
    List<RescueWorkflow> findByCompanyId(Long companyId);

    /**
     * Tìm tất cả workflows theo trạng thái
     */
    List<RescueWorkflow> findByWorkflowStatus(WorkflowStatus status);

    /**
     * Tìm tất cả workflows theo company ID và trạng thái
     */
    List<RescueWorkflow> findByCompanyIdAndWorkflowStatus(Long companyId, WorkflowStatus status);

    /**
     * Tìm tất cả workflows đang hoạt động (chưa hoàn thành, chưa hủy, chưa thất
     * bại)
     */
    @Query("SELECT w FROM RescueWorkflow w WHERE w.workflowStatus NOT IN ('COMPLETED', 'CANCELLED', 'FAILED')")
    List<RescueWorkflow> findAllActiveWorkflows();

    /**
     * Tìm tất cả workflows đang hoạt động của một công ty
     */
    @Query("SELECT w FROM RescueWorkflow w WHERE w.company.id = :companyId AND w.workflowStatus NOT IN ('COMPLETED', 'CANCELLED', 'FAILED')")
    List<RescueWorkflow> findActiveWorkflowsByCompanyId(@Param("companyId") Long companyId);

    /**
     * Tìm workflows theo user ID (thông qua rescue request)
     */
    @Query("SELECT w FROM RescueWorkflow w WHERE w.rescueRequest.user.id = :userId")
    List<RescueWorkflow> findByUserId(@Param("userId") Long userId);

    /**
     * Đếm số workflows theo trạng thái
     */
    Long countByWorkflowStatus(WorkflowStatus status);

    /**
     * Đếm số workflows đang hoạt động của một công ty
     */
    @Query("SELECT COUNT(w) FROM RescueWorkflow w WHERE w.company.id = :companyId AND w.workflowStatus NOT IN ('COMPLETED', 'CANCELLED', 'FAILED')")
    Long countActiveWorkflowsByCompanyId(@Param("companyId") Long companyId);

    /**
     * Kiểm tra workflow có tồn tại theo rescue request ID
     */
    boolean existsByRescueRequestId(Long rescueRequestId);
}
