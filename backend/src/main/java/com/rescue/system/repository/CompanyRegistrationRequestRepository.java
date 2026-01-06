package com.rescue.system.repository;

import com.rescue.system.entity.CompanyRegistrationRequest;
import com.rescue.system.entity.CompanyRegistrationStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRegistrationRequestRepository extends JpaRepository<CompanyRegistrationRequest, Long> {

    boolean existsByAccountIdAndStatus(Long accountId, CompanyRegistrationStatus status);

    Optional<CompanyRegistrationRequest> findTopByAccountIdOrderBySubmittedAtDesc(Long accountId);

    Page<CompanyRegistrationRequest> findByStatus(CompanyRegistrationStatus status, Pageable pageable);
}
