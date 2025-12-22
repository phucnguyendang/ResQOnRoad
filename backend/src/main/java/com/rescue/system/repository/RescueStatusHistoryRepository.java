package com.rescue.system.repository;

import com.rescue.system.entity.RescueStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RescueStatusHistoryRepository extends JpaRepository<RescueStatusHistory, Long> {
    
    List<RescueStatusHistory> findByRescueRequestId(Long rescueRequestId);
    
    List<RescueStatusHistory> findByRescueRequestIdOrderByChangedAtDesc(Long rescueRequestId);
}
