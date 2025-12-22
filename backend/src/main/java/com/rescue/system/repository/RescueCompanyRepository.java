package com.rescue.system.repository;

import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueCompanyRepository extends JpaRepository<RescueCompany, Long> {

        /**
         * Tìm kiếm công ty cứu hộ gần vị trí người dùng
         * SQLite không hỗ trợ math functions, dùng Euclidean approximation
         * 1 độ latitude ~ 111km, 1 độ longitude ~ 111km * cos(lat)
         * 
         * @param userLat     Vĩ độ người dùng
         * @param userLng     Kinh độ người dùng
         * @param maxDistance Bán kính tìm kiếm (km)
         * @param pageable    Phân trang
         * @return Danh sách công ty kèm khoảng cách
         */
        @Query(value = "SELECT c.id, c.name, c.address, c.phone, c.email, c.latitude, c.longitude, " +
                        "c.service_radius, c.is_active, c.average_rating, c.total_reviews, " +
                        "c.description, c.business_license, c.is_verified, c.created_at, c.updated_at, " +
                        "(111.0 * sqrt((c.latitude - :userLat) * (c.latitude - :userLat) + " +
                        "(c.longitude - :userLng) * (c.longitude - :userLng) * 0.7)) AS distance " +
                        "FROM rescue_companies c " +
                        "WHERE c.is_active = 1 " +
                        "AND (111.0 * sqrt((c.latitude - :userLat) * (c.latitude - :userLat) + " +
                        "(c.longitude - :userLng) * (c.longitude - :userLng) * 0.7)) <= :maxDistance " +
                        "ORDER BY distance ASC", countQuery = "SELECT COUNT(*) FROM rescue_companies c WHERE c.is_active = 1", nativeQuery = true)
        Page<Object[]> findNearbyCompanies(
                        @Param("userLat") Double userLat,
                        @Param("userLng") Double userLng,
                        @Param("maxDistance") Double maxDistance,
                        Pageable pageable);

        /**
         * Tìm kiếm công ty theo loại dịch vụ và vị trí
         * SQLite compatible version
         */
        @Query(value = "SELECT DISTINCT c.id, c.name, c.address, c.phone, c.email, c.latitude, c.longitude, " +
                        "c.service_radius, c.is_active, c.average_rating, c.total_reviews, " +
                        "c.description, c.business_license, c.is_verified, c.created_at, c.updated_at, " +
                        "(111.0 * sqrt((c.latitude - :userLat) * (c.latitude - :userLat) + " +
                        "(c.longitude - :userLng) * (c.longitude - :userLng) * 0.7)) AS distance " +
                        "FROM rescue_companies c " +
                        "INNER JOIN services s ON c.id = s.company_id " +
                        "WHERE c.is_active = 1 " +
                        "AND s.type IN (:serviceTypes) " +
                        "AND s.is_available = 1 " +
                        "AND (111.0 * sqrt((c.latitude - :userLat) * (c.latitude - :userLat) + " +
                        "(c.longitude - :userLng) * (c.longitude - :userLng) * 0.7)) <= :maxDistance " +
                        "ORDER BY distance ASC", countQuery = "SELECT COUNT(DISTINCT c.id) FROM rescue_companies c " +
                                        "INNER JOIN services s ON c.id = s.company_id " +
                                        "WHERE c.is_active = 1 AND s.type IN (:serviceTypes)", nativeQuery = true)
        Page<Object[]> findNearbyCompaniesByServiceTypes(
                        @Param("userLat") Double userLat,
                        @Param("userLng") Double userLng,
                        @Param("maxDistance") Double maxDistance,
                        @Param("serviceTypes") List<String> serviceTypes,
                        Pageable pageable);

        /**
         * Lấy thông tin chi tiết công ty kèm services và reviews
         */
        @Query("SELECT c FROM RescueCompany c " +
                        "LEFT JOIN FETCH c.services " +
                        "WHERE c.id = :companyId")
        Optional<RescueCompany> findByIdWithServices(@Param("companyId") Long companyId);

        @Query("SELECT c FROM RescueCompany c " +
                        "LEFT JOIN FETCH c.reviews r " +
                        "LEFT JOIN FETCH r.user " +
                        "WHERE c.id = :companyId")
        Optional<RescueCompany> findByIdWithReviews(@Param("companyId") Long companyId);
}
