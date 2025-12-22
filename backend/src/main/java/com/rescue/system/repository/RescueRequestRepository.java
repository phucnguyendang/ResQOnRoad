package com.rescue.system.repository;

import com.rescue.system.entity.RescueRequest;
import com.rescue.system.entity.RescueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueRequestRepository extends JpaRepository<RescueRequest, Long> {
    
    List<RescueRequest> findByCompanyId(Long companyId);
    
    List<RescueRequest> findByUserId(Long userId);
    
    List<RescueRequest> findByStatus(RescueStatus status);
    
    List<RescueRequest> findByCompanyIdAndStatus(Long companyId, RescueStatus status);
    
    List<RescueRequest> findByUserIdAndStatus(Long userId, RescueStatus status);
    
    Optional<RescueRequest> findById(Long id);
}
