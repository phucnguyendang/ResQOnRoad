package com.rescue.system.service;

import com.rescue.system.dto.request.ReportFilterRequest;
import com.rescue.system.dto.response.ReportStatisticsResponse;
import java.io.ByteArrayOutputStream;

public interface ReportService {

    /**
     * Lấy báo cáo thống kê theo các tiêu chí lọc
     */
    ReportStatisticsResponse getReportStatistics(ReportFilterRequest filter);

    /**
     * Lấy báo cáo theo ngày (daily report)
     */
    ReportStatisticsResponse getDailyReport(String startDate, String endDate);

    /**
     * Lấy báo cáo theo tháng (monthly report)
     */
    ReportStatisticsResponse getMonthlyReport(Integer month, Integer year);

    /**
     * Lấy báo cáo theo công ty (company report)
     */
    ReportStatisticsResponse getCompanyReport(Long companyId, String startDate, String endDate);

    /**
     * Lấy báo cáo theo dịch vụ (service report)
     */
    ReportStatisticsResponse getServiceReport(Long serviceId, String startDate, String endDate);

    /**
     * Lấy báo cáo độ hài lòng (satisfaction report)
     */
    ReportStatisticsResponse getSatisfactionReport(String startDate, String endDate);

    /**
     * Xuất báo cáo dưới dạng CSV
     */
    ByteArrayOutputStream exportReportAsCSV(ReportFilterRequest filter);

    /**
     * Xuất báo cáo dưới dạng PDF
     */
    ByteArrayOutputStream exportReportAsPDF(ReportFilterRequest filter);

    /**
     * Lấy danh sách các loại báo cáo có sẵn
     */
    java.util.List<String> getAvailableReportTypes();
}
