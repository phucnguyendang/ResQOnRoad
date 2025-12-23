package com.rescue.system.repository;

import com.rescue.system.entity.RescueRequestTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueRequestTrackingRepository extends JpaRepository<RescueRequestTracking, Long> {
    
    List<RescueRequestTracking> findByRescueRequestIdOrderByUpdatedAtDesc(Long rescueRequestId);
    
    @Query("SELECT t FROM RescueRequestTracking t WHERE t.rescueRequest.id = :rescueRequestId ORDER BY t.updatedAt DESC LIMIT 1")
    Optional<RescueRequestTracking> findLatestTracking(@Param("rescueRequestId") Long rescueRequestId);
    
    List<RescueRequestTracking> findByRescueRequestIdAndUpdatedByOrderByUpdatedAtDesc(Long rescueRequestId, Long updatedBy);
}
