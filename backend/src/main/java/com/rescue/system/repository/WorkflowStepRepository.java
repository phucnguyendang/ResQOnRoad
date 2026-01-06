package com.rescue.system.repository;

import com.rescue.system.entity.StepStatus;
import com.rescue.system.entity.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {

    /**
     * Tìm tất cả các bước theo workflow ID, sắp xếp theo số thứ tự
     */
    List<WorkflowStep> findByWorkflowIdOrderByStepNumberAsc(Long workflowId);

    /**
     * Tìm bước theo workflow ID và số thứ tự
     */
    Optional<WorkflowStep> findByWorkflowIdAndStepNumber(Long workflowId, Integer stepNumber);

    /**
     * Tìm bước hiện tại đang thực hiện
     */
    Optional<WorkflowStep> findFirstByWorkflowIdAndStepStatus(Long workflowId, StepStatus stepStatus);

    /**
     * Đếm số bước trong workflow
     */
    Long countByWorkflowId(Long workflowId);

    /**
     * Đếm số bước đã hoàn thành trong workflow
     */
    Long countByWorkflowIdAndStepStatus(Long workflowId, StepStatus stepStatus);

    /**
     * Tìm bước tiếp theo chưa bắt đầu
     */
    @Query("SELECT s FROM WorkflowStep s WHERE s.workflow.id = :workflowId AND s.stepStatus = 'PENDING' ORDER BY s.stepNumber ASC")
    List<WorkflowStep> findNextPendingSteps(@Param("workflowId") Long workflowId);

    /**
     * Xóa tất cả các bước của một workflow
     */
    void deleteByWorkflowId(Long workflowId);

    /**
     * Tìm số thứ tự bước lớn nhất trong workflow
     */
    @Query("SELECT MAX(s.stepNumber) FROM WorkflowStep s WHERE s.workflow.id = :workflowId")
    Optional<Integer> findMaxStepNumberByWorkflowId(@Param("workflowId") Long workflowId);
}
