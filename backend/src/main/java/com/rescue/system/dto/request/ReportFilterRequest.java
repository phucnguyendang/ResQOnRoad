package com.rescue.system.dto.request;

import jakarta.validation.constraints.*;

public class ReportFilterRequest {

    @NotNull(message = "Loại báo cáo không được để trống")
    private String reportType; // DAILY, MONTHLY, BY_COMPANY, BY_SERVICE, SATISFACTION

    private String startDate; // yyyy-MM-dd format
    private String endDate;   // yyyy-MM-dd format

    @Min(value = 1, message = "Tháng phải >= 1")
    @Max(value = 12, message = "Tháng phải <= 12")
    private Integer month;

    @Min(value = 2024, message = "Năm không hợp lệ")
    private Integer year;

    @Min(value = 1, message = "ID công ty phải > 0")
    private Long companyId;

    @Min(value = 1, message = "ID dịch vụ phải > 0")
    private Long serviceId;

    @Min(value = 0, message = "Page phải >= 0")
    private Integer page = 0;

    @Min(value = 1, message = "Size phải >= 1")
    @Max(value = 100, message = "Size không vượt quá 100")
    private Integer size = 20;

    // Getters and Setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
