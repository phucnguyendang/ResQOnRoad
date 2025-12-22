package com.rescue.system.service.impl;

import com.rescue.system.dto.request.CompanySearchRequest;
import com.rescue.system.dto.response.*;
import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.Review;
import com.rescue.system.entity.Service;
import com.rescue.system.entity.ServiceType;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.RescueCompanyRepository;
import com.rescue.system.service.CompanySearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class CompanySearchServiceImpl implements CompanySearchService {

        private static final Logger log = LoggerFactory.getLogger(CompanySearchServiceImpl.class);
        private final RescueCompanyRepository companyRepository;

        public CompanySearchServiceImpl(RescueCompanyRepository companyRepository) {
                this.companyRepository = companyRepository;
        }

        @Override
        public Page<CompanySearchResponse> searchNearbyCompanies(CompanySearchRequest request) {
                log.info("Searching companies near lat={}, lng={}, maxDistance={}km",
                                request.getLatitude(), request.getLongitude(), request.getMaxDistance());

                // Validate coordinates
                validateCoordinates(request.getLatitude(), request.getLongitude());

                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
                Page<Object[]> results;

                // Tìm kiếm theo service types nếu có
                if (request.getServiceTypes() != null && !request.getServiceTypes().isEmpty()) {
                        List<String> serviceTypeNames = request.getServiceTypes().stream()
                                        .map(Enum::name)
                                        .collect(Collectors.toList());

                        results = companyRepository.findNearbyCompaniesByServiceTypes(
                                        request.getLatitude(),
                                        request.getLongitude(),
                                        request.getMaxDistance(),
                                        serviceTypeNames,
                                        pageable);
                } else {
                        results = companyRepository.findNearbyCompanies(
                                        request.getLatitude(),
                                        request.getLongitude(),
                                        request.getMaxDistance(),
                                        pageable);
                }

                // Map results to DTO
                List<CompanySearchResponse> companies = results.getContent().stream()
                                .map(this::mapToCompanySearchResponse)
                                .collect(Collectors.toList());

                log.info("Found {} companies within {}km radius", companies.size(), request.getMaxDistance());

                return new PageImpl<>(companies, pageable, results.getTotalElements());
        }

        @Override
        public CompanyDetailResponse getCompanyDetails(Long companyId) {
                log.info("Getting details for company id={}", companyId);

                // Fetch company with services
                RescueCompany company = companyRepository.findByIdWithServices(companyId)
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                                                "Không tìm thấy công ty với ID: " + companyId));

                // Fetch reviews separately to avoid MultipleBagFetchException
                RescueCompany companyWithReviews = companyRepository.findByIdWithReviews(companyId)
                                .orElse(company);

                return mapToCompanyDetailResponse(company, companyWithReviews.getReviews());
        }

        // Helper methods

        private void validateCoordinates(Double latitude, Double longitude) {
                if (latitude < -90 || latitude > 90) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Latitude phải nằm trong khoảng -90 đến 90");
                }
                if (longitude < -180 || longitude > 180) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Longitude phải nằm trong khoảng -180 đến 180");
                }
        }

        private CompanySearchResponse mapToCompanySearchResponse(Object[] result) {
                try {
                        // Native query returns: [company columns..., distance]
                        int idx = 0;
                        Long id = toLong(result[idx++]);
                        String name = toSafeString(result[idx++]);
                        String address = toSafeString(result[idx++]);
                        String phone = toSafeString(result[idx++]);
                        String email = toSafeString(result[idx++]);
                        Double latitude = toDouble(result[idx++]);
                        Double longitude = toDouble(result[idx++]);
                        Double serviceRadius = toDouble(result[idx++]);
                        Boolean isActive = toBoolean(result[idx++]);
                        Double averageRating = toDouble(result[idx++]);
                        Integer totalReviews = toInteger(result[idx++]);
                        String description = toSafeString(result[idx++]);
                        String businessLicense = toSafeString(result[idx++]);
                        Boolean isVerified = toBoolean(result[idx++]);
                        // Skip timestamps
                        idx += 2; // created_at, updated_at

                        Double distance = toDouble(result[idx]); // distance từ HAVING clause

                        // Fetch services for this company
                        RescueCompany company = companyRepository.findByIdWithServices(id).orElse(null);
                        List<ServiceSummary> services = company != null ? company.getServices().stream()
                                        .map(this::mapToServiceSummary)
                                        .collect(Collectors.toList()) : new ArrayList<>();

                        return CompanySearchResponse.builder()
                                        .id(id)
                                        .name(name)
                                        .address(address)
                                        .phone(phone)
                                        .email(email)
                                        .distance(Math.round(distance * 100.0) / 100.0) // Round to 2 decimals
                                        .averageRating(averageRating)
                                        .totalReviews(totalReviews)
                                        .isAvailable(isActive)
                                        .isVerified(isVerified)
                                        .description(description)
                                        .services(services)
                                        .latitude(latitude)
                                        .longitude(longitude)
                                        .build();
                } catch (Exception e) {
                        log.error("Error mapping company search response", e);
                        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Lỗi khi xử lý dữ liệu công ty: " + e.getMessage());
                }
        }

        // Helper methods for safe type conversion
        private Long toLong(Object value) {
                if (value == null)
                        return null;
                if (value instanceof Long)
                        return (Long) value;
                if (value instanceof Integer)
                        return ((Integer) value).longValue();
                if (value instanceof BigInteger)
                        return ((BigInteger) value).longValue();
                return Long.parseLong(value.toString());
        }

        private Double toDouble(Object value) {
                if (value == null)
                        return null;
                if (value instanceof Double)
                        return (Double) value;
                if (value instanceof Float)
                        return ((Float) value).doubleValue();
                if (value instanceof Integer)
                        return ((Integer) value).doubleValue();
                if (value instanceof Long)
                        return ((Long) value).doubleValue();
                return Double.parseDouble(value.toString());
        }

        private Integer toInteger(Object value) {
                if (value == null)
                        return null;
                if (value instanceof Integer)
                        return (Integer) value;
                if (value instanceof Long)
                        return ((Long) value).intValue();
                if (value instanceof BigInteger)
                        return ((BigInteger) value).intValue();
                return Integer.parseInt(value.toString());
        }

        private Boolean toBoolean(Object value) {
                if (value == null)
                        return false;
                if (value instanceof Boolean)
                        return (Boolean) value;
                if (value instanceof Integer)
                        return ((Integer) value) != 0;
                if (value instanceof Long)
                        return ((Long) value) != 0;
                return Boolean.parseBoolean(value.toString());
        }

        private String toSafeString(Object value) {
                if (value == null)
                        return null;
                return value.toString();
        }

        private CompanyDetailResponse mapToCompanyDetailResponse(RescueCompany company, List<Review> reviews) {
                List<ServiceSummary> services = company.getServices().stream()
                                .map(this::mapToServiceSummary)
                                .collect(Collectors.toList());

                List<ReviewDetail> reviewDetails = reviews.stream()
                                .map(this::mapToReviewDetail)
                                .collect(Collectors.toList());

                return CompanyDetailResponse.builder()
                                .id(company.getId())
                                .name(company.getName())
                                .address(company.getAddress())
                                .phone(company.getPhone())
                                .email(company.getEmail())
                                .latitude(company.getLatitude())
                                .longitude(company.getLongitude())
                                .serviceRadius(company.getServiceRadius())
                                .isActive(company.getIsActive())
                                .isVerified(company.getIsVerified())
                                .averageRating(company.getAverageRating())
                                .totalReviews(company.getTotalReviews())
                                .description(company.getDescription())
                                .businessLicense(company.getBusinessLicense())
                                .services(services)
                                .reviews(reviewDetails)
                                .build();
        }

        private ServiceSummary mapToServiceSummary(Service service) {
                return ServiceSummary.builder()
                                .id(service.getId())
                                .name(service.getName())
                                .type(service.getType().name())
                                .typeDisplayName(service.getType().getDisplayName())
                                .basePrice(service.getBasePrice())
                                .priceUnit(service.getPriceUnit())
                                .isAvailable(service.getIsAvailable())
                                .estimatedTime(service.getEstimatedTime())
                                .build();
        }

        private ReviewDetail mapToReviewDetail(Review review) {
                return ReviewDetail.builder()
                                .id(review.getId())
                                .userName(review.getUser().getFullName())
                                .rating(review.getRating())
                                .comment(review.getComment())
                                .isVerified(review.getIsVerified())
                                .createdAt(review.getCreatedAt())
                                .build();
        }
}
