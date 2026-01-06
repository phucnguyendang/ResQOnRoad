package com.rescue.system.controller;

import com.rescue.system.dto.request.ReportFilterRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.ReportStatisticsResponse;
import com.rescue.system.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportAdminController {

    private final ReportService reportService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportAdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * UC406: Lấy danh sách loại báo cáo (Step 2)
     * GET /api/admin/reports/types
     */
    @GetMapping("/types")
    public ApiResponse<List<String>> getReportTypes() {
        List<String> types = reportService.getAvailableReportTypes();
        return ApiResponse.of("Lấy danh sách loại báo cáo thành công", types);
    }

    /**
     * UC406: Lấy báo cáo thống kê (Step 3-4)
     * POST /api/admin/reports/statistics
     * 
     * Request body:
     * {
     *   "reportType": "DAILY|MONTHLY|BY_COMPANY|BY_SERVICE|SATISFACTION",
     *   "startDate": "2026-01-01" (required for DAILY, BY_COMPANY, BY_SERVICE, SATISFACTION),
     *   "endDate": "2026-01-31",
     *   "month": 1 (required for MONTHLY),
     *   "year": 2026 (required for MONTHLY),
     *   "companyId": 1 (required for BY_COMPANY),
     *   "serviceId": 1 (required for BY_SERVICE)
     * }
     */
    @PostMapping("/statistics")
    public ApiResponse<ReportStatisticsResponse> getReportStatistics(
            @Valid @RequestBody ReportFilterRequest filter) {
        ReportStatisticsResponse report = reportService.getReportStatistics(filter);
        
        if (!report.getHasData()) {
            return ApiResponse.of("Không có dữ liệu cho báo cáo này", report);
        }
        
        return ApiResponse.of("Lấy báo cáo thành công", report);
    }

    /**
     * UC406: Lấy báo cáo theo ngày (Daily Report)
     * GET /api/admin/reports/daily?startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/daily")
    public ApiResponse<ReportStatisticsResponse> getDailyReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        // Default to last 30 days if not specified
        if (endDate == null) {
            endDate = LocalDate.now().format(DATE_FORMATTER);
        }
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30).format(DATE_FORMATTER);
        }
        
        ReportStatisticsResponse report = reportService.getDailyReport(startDate, endDate);
        
        if (!report.getHasData()) {
            return ApiResponse.of("Không có dữ liệu", report);
        }
        
        return ApiResponse.of("Lấy báo cáo theo ngày thành công", report);
    }

    /**
     * UC406: Lấy báo cáo theo tháng (Monthly Report)
     * GET /api/admin/reports/monthly?month=1&year=2026
     */
    @GetMapping("/monthly")
    public ApiResponse<ReportStatisticsResponse> getMonthlyReport(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        // Default to current month if not specified
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        
        ReportStatisticsResponse report = reportService.getMonthlyReport(month, year);
        
        if (!report.getHasData()) {
            return ApiResponse.of("Không có dữ liệu", report);
        }
        
        return ApiResponse.of("Lấy báo cáo theo tháng thành công", report);
    }

    /**
     * UC406: Lấy báo cáo theo công ty (Company Report)
     * GET /api/admin/reports/company/{companyId}?startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/company/{companyId}")
    public ApiResponse<ReportStatisticsResponse> getCompanyReport(
            @PathVariable Long companyId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        // Default to last 30 days if not specified
        if (endDate == null) {
            endDate = LocalDate.now().format(DATE_FORMATTER);
        }
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30).format(DATE_FORMATTER);
        }
        
        ReportStatisticsResponse report = reportService.getCompanyReport(companyId, startDate, endDate);
        
        if (!report.getHasData()) {
            return ApiResponse.of("Không có dữ liệu", report);
        }
        
        return ApiResponse.of("Lấy báo cáo theo công ty thành công", report);
    }

    /**
     * UC406: Lấy báo cáo theo dịch vụ (Service Report)
     * GET /api/admin/reports/service/{serviceId}?startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/service/{serviceId}")
    public ApiResponse<ReportStatisticsResponse> getServiceReport(
            @PathVariable Long serviceId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        // Default to last 30 days if not specified
        if (endDate == null) {
            endDate = LocalDate.now().format(DATE_FORMATTER);
        }
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30).format(DATE_FORMATTER);
        }
        
        ReportStatisticsResponse report = reportService.getServiceReport(serviceId, startDate, endDate);
        
        if (!report.getHasData()) {
            return ApiResponse.of("Không có dữ liệu", report);
        }
        
        return ApiResponse.of("Lấy báo cáo theo dịch vụ thành công", report);
    }

    /**
     * UC406: Lấy báo cáo độ hài lòng (Satisfaction Report)
     * GET /api/admin/reports/satisfaction?startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/satisfaction")
    public ApiResponse<ReportStatisticsResponse> getSatisfactionReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        // Default to last 30 days if not specified
        if (endDate == null) {
            endDate = LocalDate.now().format(DATE_FORMATTER);
        }
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30).format(DATE_FORMATTER);
        }
        
        ReportStatisticsResponse report = reportService.getSatisfactionReport(startDate, endDate);
        
        if (!report.getHasData()) {
            return ApiResponse.of("Không có dữ liệu", report);
        }
        
        return ApiResponse.of("Lấy báo cáo độ hài lòng thành công", report);
    }

    /**
     * UC406: Xuất báo cáo dưới dạng CSV (Step 5)
     * POST /api/admin/reports/export/csv
     * 
     * Request body:
     * {
     *   "reportType": "DAILY",
     *   "startDate": "2026-01-01",
     *   "endDate": "2026-01-31"
     * }
     */
    @PostMapping("/export/csv")
    public ResponseEntity<byte[]> exportReportAsCSV(
            @Valid @RequestBody ReportFilterRequest filter) {
        try {
            ByteArrayOutputStream outputStream = reportService.exportReportAsCSV(filter);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"bao-cao-" + filter.getReportType().toLowerCase() + ".csv\"")
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * UC406: Xuất báo cáo dưới dạng PDF (Step 5)
     * POST /api/admin/reports/export/pdf
     * 
     * Request body:
     * {
     *   "reportType": "DAILY",
     *   "startDate": "2026-01-01",
     *   "endDate": "2026-01-31"
     * }
     */
    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportReportAsPDF(
            @Valid @RequestBody ReportFilterRequest filter) {
        try {
            ByteArrayOutputStream outputStream = reportService.exportReportAsPDF(filter);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"bao-cao-" + filter.getReportType().toLowerCase() + ".pdf\"")
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
