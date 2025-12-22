package com.rescue.system.controller;

import com.rescue.system.dto.request.CompanySearchRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.CompanyDetailResponse;
import com.rescue.system.dto.response.CompanySearchResponse;
import com.rescue.system.service.CompanySearchService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanySearchController {

        private static final Logger log = LoggerFactory.getLogger(CompanySearchController.class);
        private final CompanySearchService companySearchService;

        public CompanySearchController(CompanySearchService companySearchService) {
                this.companySearchService = companySearchService;
        }

        /**
         * API tìm kiếm công ty cứu hộ gần vị trí người dùng
         * POST /api/companies/search
         */
        @PostMapping("/search")
        public ResponseEntity<ApiResponse<Page<CompanySearchResponse>>> searchNearbyCompanies(
                        @Valid @RequestBody CompanySearchRequest request) {

                log.info("POST /api/companies/search - lat={}, lng={}, maxDistance={}km",
                                request.getLatitude(), request.getLongitude(), request.getMaxDistance());

                Page<CompanySearchResponse> companies = companySearchService.searchNearbyCompanies(request);

                String message = companies.isEmpty()
                                ? "Không tìm thấy công ty cứu hộ nào trong bán kính " + request.getMaxDistance() + "km"
                                : "Tìm thấy " + companies.getTotalElements() + " công ty cứu hộ gần bạn";

                ApiResponse<Page<CompanySearchResponse>> response = new ApiResponse<>(
                                HttpStatus.OK.value(),
                                message,
                                companies);

                return ResponseEntity.ok(response);
        }

        /**
         * API tìm kiếm theo query params (alternative cho GET request)
         * GET /api/companies/search?lat=...&lng=...&maxDistance=...
         */
        @GetMapping("/search")
        public ResponseEntity<ApiResponse<Page<CompanySearchResponse>>> searchNearbyCompaniesGet(
                        @RequestParam Double lat,
                        @RequestParam Double lng,
                        @RequestParam(defaultValue = "50.0") Double maxDistance,
                        @RequestParam(defaultValue = "0") Integer page,
                        @RequestParam(defaultValue = "20") Integer size) {

                log.info("GET /api/companies/search - lat={}, lng={}, maxDistance={}km", lat, lng, maxDistance);

                CompanySearchRequest request = new CompanySearchRequest();
                request.setLatitude(lat);
                request.setLongitude(lng);
                request.setMaxDistance(maxDistance);
                request.setPage(page);
                request.setSize(size);

                Page<CompanySearchResponse> companies = companySearchService.searchNearbyCompanies(request);

                String message = companies.isEmpty()
                                ? "Không tìm thấy công ty cứu hộ nào trong bán kính " + maxDistance + "km"
                                : "Tìm thấy " + companies.getTotalElements() + " công ty cứu hộ gần bạn";

                ApiResponse<Page<CompanySearchResponse>> response = new ApiResponse<>(
                                HttpStatus.OK.value(),
                                message,
                                companies);

                return ResponseEntity.ok(response);
        }

        /**
         * API lấy thông tin chi tiết công ty
         * GET /api/companies/{companyId}
         */
        @GetMapping("/{companyId}")
        public ResponseEntity<ApiResponse<CompanyDetailResponse>> getCompanyDetails(
                        @PathVariable Long companyId) {

                log.info("GET /api/companies/{}", companyId);

                CompanyDetailResponse company = companySearchService.getCompanyDetails(companyId);

                ApiResponse<CompanyDetailResponse> response = new ApiResponse<>(
                                HttpStatus.OK.value(),
                                "Lấy thông tin công ty thành công",
                                company);

                return ResponseEntity.ok(response);
        }
}
