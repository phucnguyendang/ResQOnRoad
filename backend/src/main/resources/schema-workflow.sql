-- Schema cho UC304 - Quản lý quy trình cứu hộ (RescueWorkflow)
-- Bảng này lưu trữ thông tin chi tiết về quy trình xử lý khi nhận được yêu cầu cứu hộ

-- Bảng rescue_workflows - Quy trình cứu hộ
CREATE TABLE IF NOT EXISTS rescue_workflows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rescue_request_id BIGINT NOT NULL UNIQUE,
    company_id BIGINT,
    workflow_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    processing_steps TEXT,
    current_step VARCHAR(100),
    step_number INT DEFAULT 0,
    notes TEXT,
    estimated_arrival_time TIMESTAMP,
    actual_arrival_time TIMESTAMP,
    completion_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    
    CONSTRAINT fk_workflow_request FOREIGN KEY (rescue_request_id) REFERENCES rescue_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_workflow_company FOREIGN KEY (company_id) REFERENCES accounts(id) ON DELETE SET NULL,
    
    INDEX idx_workflow_request_id (rescue_request_id),
    INDEX idx_workflow_company_id (company_id),
    INDEX idx_workflow_status (workflow_status)
);

-- Bảng workflow_steps - Chi tiết các bước xử lý trong quy trình
CREATE TABLE IF NOT EXISTS workflow_steps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_id BIGINT NOT NULL,
    step_number INT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    step_description TEXT,
    step_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    performed_by VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    
    CONSTRAINT fk_step_workflow FOREIGN KEY (workflow_id) REFERENCES rescue_workflows(id) ON DELETE CASCADE,
    
    INDEX idx_step_workflow_id (workflow_id),
    INDEX idx_step_number (step_number)
);

-- Enum values reference:
-- workflow_status: PENDING, ASSIGNED, DISPATCHED, IN_TRANSIT, ON_SITE, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
-- step_status: PENDING, IN_PROGRESS, COMPLETED, SKIPPED, FAILED
