package com.rescue.system.service.impl;

import com.rescue.system.dto.request.ReportFilterRequest;
import com.rescue.system.dto.response.ReportStatisticsResponse;
import com.rescue.system.entity.RescueRequest;
import com.rescue.system.entity.Review;
import com.rescue.system.repository.RescueRequestRepository;
import com.rescue.system.repository.ReviewRepository;
import com.rescue.system.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final RescueRequestRepository rescueRequestRepository;
    private final ReviewRepository reviewRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public ReportServiceImpl(
            RescueRequestRepository rescueRequestRepository,
            ReviewRepository reviewRepository) {
        this.rescueRequestRepository = rescueRequestRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReportStatisticsResponse getReportStatistics(ReportFilterRequest filter) {
        if (filter.getReportType() == null || filter.getReportType().isEmpty()) {
            return createEmptyReport("Loại báo cáo không hợp lệ");
        }

        return switch (filter.getReportType().toUpperCase()) {
            case "DAILY" -> getDailyReport(filter.getStartDate(), filter.getEndDate());
            case "MONTHLY" -> getMonthlyReport(filter.getMonth(), filter.getYear());
            case "BY_COMPANY" -> getCompanyReport(filter.getCompanyId(), filter.getStartDate(), filter.getEndDate());
            case "BY_SERVICE" -> getServiceReport(filter.getServiceId(), filter.getStartDate(), filter.getEndDate());
            case "SATISFACTION" -> getSatisfactionReport(filter.getStartDate(), filter.getEndDate());
            default -> createEmptyReport("Loại báo cáo không được hỗ trợ");
        };
    }

    @Override
    public ReportStatisticsResponse getDailyReport(String startDate, String endDate) {
        ReportStatisticsResponse response = new ReportStatisticsResponse();
        response.setReportType("DAILY");

        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        Instant startInstant = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant();

        // Get all rescue requests in the date range
        List<RescueRequest> requests = rescueRequestRepository.findAll().stream()
                .filter(r -> r.getCreatedAt() != null && 
                        !r.getCreatedAt().isBefore(startInstant) && 
                        !r.getCreatedAt().isAfter(endInstant))
                .collect(Collectors.toList());

        if (requests.isEmpty()) {
            response.setHasData(false);
            response.setMessage("Không có dữ liệu");
            return response;
        }

        response.setHasData(true);
        response.setPeriod(startDate + " đến " + endDate);

        // Calculate overall statistics
        int totalRequests = requests.size();
        int completedRequests = (int) requests.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus().toString()))
                .count();
        int cancelledRequests = (int) requests.stream()
                .filter(r -> "CANCELLED".equals(r.getStatus().toString()))
                .count();

        response.setTotalRequests(totalRequests);
        response.setCompletedRequests(completedRequests);
        response.setCancelledRequests(cancelledRequests);
        response.setCompletionRate(totalRequests > 0 ? (double) completedRequests / totalRequests * 100 : 0);

        // Calculate average satisfaction
        Double avgSatisfaction = calculateAverageSatisfaction(requests);
        response.setAverageSatisfactionScore(avgSatisfaction);

        // Group by day
        List<ReportStatisticsResponse.DailyStatistics> dailyStats = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Instant dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant dayEnd = date.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant();

            List<RescueRequest> dayRequests = requests.stream()
                    .filter(r -> !r.getCreatedAt().isBefore(dayStart) && !r.getCreatedAt().isBefore(dayEnd))
                    .collect(Collectors.toList());

            if (!dayRequests.isEmpty()) {
                int dayCompleted = (int) dayRequests.stream()
                        .filter(r -> "COMPLETED".equals(r.getStatus().toString()))
                        .count();
                Double daySatisfaction = calculateAverageSatisfaction(dayRequests);

                dailyStats.add(new ReportStatisticsResponse.DailyStatistics(
                        date.format(DATE_FORMATTER),
                        dayRequests.size(),
                        dayCompleted,
                        daySatisfaction
                ));
            }
        }

        response.setDailyStats(dailyStats);
        return response;
    }

    @Override
    public ReportStatisticsResponse getMonthlyReport(Integer month, Integer year) {
        ReportStatisticsResponse response = new ReportStatisticsResponse();
        response.setReportType("MONTHLY");

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        Instant startInstant = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant();

        List<RescueRequest> requests = rescueRequestRepository.findAll().stream()
                .filter(r -> r.getCreatedAt() != null && 
                        !r.getCreatedAt().isBefore(startInstant) && 
                        !r.getCreatedAt().isAfter(endInstant))
                .collect(Collectors.toList());

        if (requests.isEmpty()) {
            response.setHasData(false);
            response.setMessage("Không có dữ liệu");
            return response;
        }

        response.setHasData(true);
        response.setPeriod(yearMonth.format(MONTH_FORMATTER));

        int totalRequests = requests.size();
        int completedRequests = (int) requests.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus().toString()))
                .count();
        int cancelledRequests = (int) requests.stream()
                .filter(r -> "CANCELLED".equals(r.getStatus().toString()))
                .count();

        response.setTotalRequests(totalRequests);
        response.setCompletedRequests(completedRequests);
        response.setCancelledRequests(cancelledRequests);
        response.setCompletionRate(totalRequests > 0 ? (double) completedRequests / totalRequests * 100 : 0);

        Double avgSatisfaction = calculateAverageSatisfaction(requests);
        response.setAverageSatisfactionScore(avgSatisfaction);

        return response;
    }

    @Override
    public ReportStatisticsResponse getCompanyReport(Long companyId, String startDate, String endDate) {
        ReportStatisticsResponse response = new ReportStatisticsResponse();
        response.setReportType("BY_COMPANY");

        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        Instant startInstant = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant();

        List<RescueRequest> requests = rescueRequestRepository.findAll().stream()
                .filter(r -> r.getCompany() != null && r.getCompany().getId().equals(companyId) &&
                        r.getCreatedAt() != null && 
                        !r.getCreatedAt().isBefore(startInstant) && 
                        !r.getCreatedAt().isAfter(endInstant))
                .collect(Collectors.toList());

        if (requests.isEmpty()) {
            response.setHasData(false);
            response.setMessage("Không có dữ liệu");
            return response;
        }

        response.setHasData(true);
        response.setPeriod(startDate + " đến " + endDate);

        int totalRequests = requests.size();
        int completedRequests = (int) requests.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus().toString()))
                .count();

        response.setTotalRequests(totalRequests);
        response.setCompletedRequests(completedRequests);
        response.setCompletionRate(totalRequests > 0 ? (double) completedRequests / totalRequests * 100 : 0);

        Double avgSatisfaction = calculateAverageSatisfaction(requests);
        response.setAverageSatisfactionScore(avgSatisfaction);

        return response;
    }

    @Override
    public ReportStatisticsResponse getServiceReport(Long serviceId, String startDate, String endDate) {
        ReportStatisticsResponse response = new ReportStatisticsResponse();
        response.setReportType("BY_SERVICE");

        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        Instant startInstant = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant();

        List<RescueRequest> requests = rescueRequestRepository.findAll().stream()
                .filter(r -> r.getCreatedAt() != null && 
                        !r.getCreatedAt().isBefore(startInstant) && 
                        !r.getCreatedAt().isAfter(endInstant))
                .collect(Collectors.toList());

        if (requests.isEmpty()) {
            response.setHasData(false);
            response.setMessage("Không có dữ liệu");
            return response;
        }

        response.setHasData(true);
        response.setPeriod(startDate + " đến " + endDate);

        int totalRequests = requests.size();
        response.setTotalRequests(totalRequests);

        return response;
    }

    @Override
    public ReportStatisticsResponse getSatisfactionReport(String startDate, String endDate) {
        ReportStatisticsResponse response = new ReportStatisticsResponse();
        response.setReportType("SATISFACTION");

        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        List<Review> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getCreatedAt() != null && 
                        !r.getCreatedAt().isBefore(startDateTime) && 
                        !r.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());

        if (reviews.isEmpty()) {
            response.setHasData(false);
            response.setMessage("Không có dữ liệu");
            return response;
        }

        response.setHasData(true);
        response.setPeriod(startDate + " đến " + endDate);

        Double avgRating = reviews.stream()
                .mapToDouble(r -> r.getRating() != null ? r.getRating().doubleValue() : 0)
                .average()
                .orElse(0.0);

        response.setAverageSatisfactionScore(avgRating);

        // Create satisfaction breakdown
        ReportStatisticsResponse.SatisfactionBreakdown breakdown = new ReportStatisticsResponse.SatisfactionBreakdown();
        breakdown.setFiveStar((int) reviews.stream().filter(r -> r.getRating() == 5).count());
        breakdown.setFourStar((int) reviews.stream().filter(r -> r.getRating() == 4).count());
        breakdown.setThreeStar((int) reviews.stream().filter(r -> r.getRating() == 3).count());
        breakdown.setTwoStar((int) reviews.stream().filter(r -> r.getRating() == 2).count());
        breakdown.setOneStar((int) reviews.stream().filter(r -> r.getRating() == 1).count());

        response.setSatisfactionBreakdown(breakdown);
        response.setTotalRequests(reviews.size());

        return response;
    }

    @Override
    public ByteArrayOutputStream exportReportAsCSV(ReportFilterRequest filter) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        ReportStatisticsResponse report = getReportStatistics(filter);

        writer.println("BÁO CÁO THỐNG KÊ - " + report.getReportType());
        writer.println("Thời gian: " + report.getPeriod());
        writer.println("Ngày tạo: " + report.getGeneratedAt());
        writer.println();

        if (!report.getHasData()) {
            writer.println("Không có dữ liệu");
            writer.flush();
            return outputStream;
        }

        writer.println("Tổng số yêu cầu," + report.getTotalRequests());
        writer.println("Yêu cầu hoàn thành," + report.getCompletedRequests());
        writer.println("Yêu cầu bị hủy," + report.getCancelledRequests());
        writer.println("Tỷ lệ hoàn thành (%)," + String.format("%.2f", report.getCompletionRate()));
        writer.println("Điểm hài lòng trung bình," + String.format("%.2f", report.getAverageSatisfactionScore()));

        writer.flush();
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportReportAsPDF(ReportFilterRequest filter) {
        // Simplified PDF export - in production, use a proper PDF library
        return exportReportAsCSV(filter);
    }

    @Override
    public List<String> getAvailableReportTypes() {
        return Arrays.asList(
                "DAILY",
                "MONTHLY",
                "BY_COMPANY",
                "BY_SERVICE",
                "SATISFACTION"
        );
    }

    private Double calculateAverageSatisfaction(List<RescueRequest> requests) {
        if (requests.isEmpty()) {
            return 0.0;
        }

        double totalRating = 0;
        int count = 0;

        for (RescueRequest request : requests) {
            if (request.getId() != null) {
                List<Review> reviews = reviewRepository.findAll().stream()
                        .collect(Collectors.toList());
                
                if (!reviews.isEmpty()) {
                    for (Review review : reviews) {
                        if (review.getRating() != null) {
                            totalRating += review.getRating();
                            count++;
                        }
                    }
                }
            }
        }

        return count > 0 ? totalRating / count : 0.0;
    }

    private ReportStatisticsResponse createEmptyReport(String message) {
        ReportStatisticsResponse response = new ReportStatisticsResponse();
        response.setHasData(false);
        response.setMessage(message);
        return response;
    }
}
