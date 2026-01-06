package com.rescue.system.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReportStatisticsResponse {

    private String reportType;
    private String period; // e.g., "2026-01" for monthly
    private LocalDateTime generatedAt;
    
    // Overall statistics
    private Integer totalRequests;
    private Integer completedRequests;
    private Integer cancelledRequests;
    private Double completionRate;
    private Double averageSatisfactionScore;
    
    // Daily/Monthly breakdown
    private List<DailyStatistics> dailyStats;
    private List<MonthlyStatistics> monthlyStats;
    
    // Company statistics
    private List<CompanyStatistics> companyStats;
    
    // Service statistics
    private List<ServiceStatistics> serviceStats;
    
    // Time-based statistics
    private Integer averageResponseTime; // in minutes
    private Integer averageCompletionTime; // in minutes
    
    // Satisfaction breakdown
    private SatisfactionBreakdown satisfactionBreakdown;
    
    private Boolean hasData;
    private String message;

    public ReportStatisticsResponse() {
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getCompletedRequests() {
        return completedRequests;
    }

    public void setCompletedRequests(Integer completedRequests) {
        this.completedRequests = completedRequests;
    }

    public Integer getCancelledRequests() {
        return cancelledRequests;
    }

    public void setCancelledRequests(Integer cancelledRequests) {
        this.cancelledRequests = cancelledRequests;
    }

    public Double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }

    public Double getAverageSatisfactionScore() {
        return averageSatisfactionScore;
    }

    public void setAverageSatisfactionScore(Double averageSatisfactionScore) {
        this.averageSatisfactionScore = averageSatisfactionScore;
    }

    public List<DailyStatistics> getDailyStats() {
        return dailyStats;
    }

    public void setDailyStats(List<DailyStatistics> dailyStats) {
        this.dailyStats = dailyStats;
    }

    public List<MonthlyStatistics> getMonthlyStats() {
        return monthlyStats;
    }

    public void setMonthlyStats(List<MonthlyStatistics> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }

    public List<CompanyStatistics> getCompanyStats() {
        return companyStats;
    }

    public void setCompanyStats(List<CompanyStatistics> companyStats) {
        this.companyStats = companyStats;
    }

    public List<ServiceStatistics> getServiceStats() {
        return serviceStats;
    }

    public void setServiceStats(List<ServiceStatistics> serviceStats) {
        this.serviceStats = serviceStats;
    }

    public Integer getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Integer averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Integer getAverageCompletionTime() {
        return averageCompletionTime;
    }

    public void setAverageCompletionTime(Integer averageCompletionTime) {
        this.averageCompletionTime = averageCompletionTime;
    }

    public SatisfactionBreakdown getSatisfactionBreakdown() {
        return satisfactionBreakdown;
    }

    public void setSatisfactionBreakdown(SatisfactionBreakdown satisfactionBreakdown) {
        this.satisfactionBreakdown = satisfactionBreakdown;
    }

    public Boolean getHasData() {
        return hasData;
    }

    public void setHasData(Boolean hasData) {
        this.hasData = hasData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Inner classes
    public static class DailyStatistics {
        private String date;
        private Integer totalRequests;
        private Integer completedRequests;
        private Double averageSatisfaction;

        public DailyStatistics(String date, Integer totalRequests, Integer completedRequests, Double averageSatisfaction) {
            this.date = date;
            this.totalRequests = totalRequests;
            this.completedRequests = completedRequests;
            this.averageSatisfaction = averageSatisfaction;
        }

        public String getDate() { return date; }
        public Integer getTotalRequests() { return totalRequests; }
        public Integer getCompletedRequests() { return completedRequests; }
        public Double getAverageSatisfaction() { return averageSatisfaction; }
    }

    public static class MonthlyStatistics {
        private String month;
        private Integer totalRequests;
        private Integer completedRequests;
        private Double averageSatisfaction;

        public MonthlyStatistics(String month, Integer totalRequests, Integer completedRequests, Double averageSatisfaction) {
            this.month = month;
            this.totalRequests = totalRequests;
            this.completedRequests = completedRequests;
            this.averageSatisfaction = averageSatisfaction;
        }

        public String getMonth() { return month; }
        public Integer getTotalRequests() { return totalRequests; }
        public Integer getCompletedRequests() { return completedRequests; }
        public Double getAverageSatisfaction() { return averageSatisfaction; }
    }

    public static class CompanyStatistics {
        private Long companyId;
        private String companyName;
        private Integer totalRequests;
        private Integer completedRequests;
        private Double completionRate;
        private Double averageRating;
        private Double averageResponseTime;

        public CompanyStatistics() {}

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public Integer getTotalRequests() { return totalRequests; }
        public void setTotalRequests(Integer totalRequests) { this.totalRequests = totalRequests; }
        public Integer getCompletedRequests() { return completedRequests; }
        public void setCompletedRequests(Integer completedRequests) { this.completedRequests = completedRequests; }
        public Double getCompletionRate() { return completionRate; }
        public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
        public Double getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(Double averageResponseTime) { this.averageResponseTime = averageResponseTime; }
    }

    public static class ServiceStatistics {
        private Long serviceId;
        private String serviceName;
        private Integer usageCount;
        private Double averagePrice;
        private Double averageRating;

        public ServiceStatistics() {}

        public Long getServiceId() { return serviceId; }
        public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public Integer getUsageCount() { return usageCount; }
        public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
        public Double getAveragePrice() { return averagePrice; }
        public void setAveragePrice(Double averagePrice) { this.averagePrice = averagePrice; }
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    }

    public static class SatisfactionBreakdown {
        private Integer fiveStar;
        private Integer fourStar;
        private Integer threeStar;
        private Integer twoStar;
        private Integer oneStar;

        public SatisfactionBreakdown() {}

        public Integer getFiveStar() { return fiveStar; }
        public void setFiveStar(Integer fiveStar) { this.fiveStar = fiveStar; }
        public Integer getFourStar() { return fourStar; }
        public void setFourStar(Integer fourStar) { this.fourStar = fourStar; }
        public Integer getThreeStar() { return threeStar; }
        public void setThreeStar(Integer threeStar) { this.threeStar = threeStar; }
        public Integer getTwoStar() { return twoStar; }
        public void setTwoStar(Integer twoStar) { this.twoStar = twoStar; }
        public Integer getOneStar() { return oneStar; }
        public void setOneStar(Integer oneStar) { this.oneStar = oneStar; }
    }
}
